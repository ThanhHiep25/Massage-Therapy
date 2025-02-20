package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.Role;
import com.example.spa.entities.User;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.exception.UserStatus;
import com.example.spa.repositories.UserRepository;
import com.example.spa.services.RoleService;
import com.example.spa.services.UserService;
import com.example.spa.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserResponse register(UserRegisterRequest user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User name already registered");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Tạo OTP và gửi qua email
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(user.getEmail(), otp);

        // Lưu thông tin đăng ký tạm thời vào cơ sở dữ liệu hoặc bộ nhớ
        otpService.savePendingUser(user);

        return new UserResponse().builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .address(user.getAddress())
                .role(user.getRole().getRoleName())
                .build();
    }


    public UserResponse verifyOtp(String email, String otp) {
        String verificationResult = otpService.verifyOtp(email, otp);

        if (!"Xác thực thành công".equals(verificationResult)) {
            throw new RuntimeException(verificationResult);
        }

        // Lấy thông tin đăng ký tạm thời
        UserRegisterRequest pendingUser = otpService.getPendingUser(email);
        if (pendingUser == null) {
            throw new RuntimeException("No pending registration found for this email");
        }

        // Kiểm tra xem role có hợp lệ hay không trước khi lưu vào cơ sở dữ liệu
        if (pendingUser.getRole() == null) {
            // Tạo vai trò mặc định nếu không có
            Role defaultRole = roleService.findByName("superadmin");
            if (defaultRole == null) {
                throw new RuntimeException("Role 'superadmin' not found.");
            }
            pendingUser.setRole(defaultRole);
        }

        // Lưu người dùng vào cơ sở dữ liệu, chỉ sau khi OTP xác thực thành công
        User userToSave = User.builder()
                .username(pendingUser.getUsername())
                .email(pendingUser.getEmail())
                .phone(pendingUser.getPhone())
                .name(pendingUser.getName())
                .password(passwordEncoder.encode(pendingUser.getPassword()))
                .address(pendingUser.getAddress())
                .createdAt(pendingUser.getCreatedAt())
                .updatedAt(pendingUser.getUpdatedAt())
                .imageUrl(pendingUser.getImageUrl())
                .description(pendingUser.getDescription())
                .status(UserStatus.ACTIVE) // Mặc định là ACTIVE
                .role(pendingUser.getRole()) // Đảm bảo role được gán chính xác
                .build();
        userRepository.save(userToSave);

        // Xóa thông tin tạm thời sau khi đăng ký thành công
        otpService.clearPendingUser(email);

        return new UserResponse().builder()
                .username(userToSave.getUsername())
                .email(userToSave.getEmail())
                .phone(userToSave.getPhone())
                .name(userToSave.getName())
                .role(userToSave.getRole() != null ? userToSave.getRole().getRoleName() : "No Role Assigned")
                .build();
    }


    @Override
    public Map<String, Object> login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        // Chuẩn bị dữ liệu phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", Map.of(
                "id", user.getUserId(),
                "username", user.getUsername(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "address", user.getAddress(),
                "imageUrl", user.getImageUrl(),
                "createdAt", user.getCreatedAt(),
                "description", user.getDescription(),
                "roles", user.getRole().getRoleName()
        ));

        return response;
    }


    @Override
    public String refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        if (username != null || !jwtUtil.isTokenValid(refreshToken, username)) {
            throw new RuntimeException("Invalid refresh token");
        }
        return jwtUtil.generateToken(username);
    }

    @Override
    public String forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(email, otp);
        return "OTP sent to email";
    }

    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        // Kiểm tra OTP
        String verificationResult = otpService.verifyOtp(email, otp);
        if (!"Xác thực thành công".equals(verificationResult)) {
            // Ném lỗi chi tiết để frontend xử lý dễ hơn
            throw new IllegalArgumentException(verificationResult);
        }
        // Tìm kiếm người dùng theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa OTP sau khi reset thành công để tránh tái sử dụng
        otpService.clearPendingUser(email);

        return "Đặt lại mật khẩu thành công";
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật thông tin người dùng nếu có dữ liệu mới
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("Email already exists!");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }

        // Cập nhật mật khẩu nếu người dùng gửi mật khẩu mới
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .address(user.getAddress())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .role(user.getRole().getRoleName())
                .build();
    }


    @Override
    public GetUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        log.info("GetUserResponse: {}", id);
        return new GetUserResponse().builder()
                .email(user.getEmail())
                .name(user.getName())
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .imageUrl(user.getImageUrl())
                .createdAt(user.getCreatedAt().toString())
                .role(user.getRole().getRoleName())
                .build();
    }

    @Override
    public User findByUsernameOrEmail(String identifier) {
        Optional<User> user = userRepository.findByUsernameOrEmail(identifier, identifier);
        return user.orElse(null);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has a "superadmin" role
        if ("superadmin".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new IllegalArgumentException("Cannot delete user with superadmin role");
        }

        // Proceed to delete if the user is not a superadmin
        userRepository.deleteById(id);
    }

    @Override
    public String extractTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Nếu dùng HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Xóa cookie bằng cách đặt maxAge là 0
        response.addCookie(cookie);
    }


    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        user.setStatus(UserStatus.DELETED); // Chỉ đánh dấu là đã "xóa"
        userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        user.setStatus(UserStatus.DEACTIVATED); // Chuyển trạng thái thành DEACTIVATED
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        user.setStatus(UserStatus.ACTIVE); // Kích hoạt lại user
        userRepository.save(user);
    }



}