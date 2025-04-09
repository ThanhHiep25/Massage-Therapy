package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserLoginResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.Role;
import com.example.spa.entities.User;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.enums.UserStatus;
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

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        // Lấy role từ database thay vì gán trực tiếp từ request
        Role role = roleService.findByName(user.getRole() != null ? user.getRole().getRoleName() : "customer");
        if (role == null) {
            throw new RuntimeException("Role not found.");
        }

        // Tạo OTP và gửi qua email
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(user.getEmail(), otp);

        // Gán lại role lấy từ database
        user.setRole(role);

        // Lưu thông tin đăng ký tạm thời
        otpService.savePendingUser(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .address(user.getAddress())
                .role(role.getRoleName()) // ✅ Sử dụng role đã lấy từ DB
                .build();
    }

    @Override
    public UserResponse createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        // Lấy role từ database thay vì gán trực tiếp từ request
        Role role = roleService.findByName(request.getRole() != null ? request.getRole().getRoleName() : "customer");
        if (role == null) {
            throw new RuntimeException("Role not found.");
        }

        // Lưu người dùng vào cơ sở dữ liệu
        User userToSave = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .status(UserStatus.ACTIVATE) // Mặc định là ACTIVE
                .role(role) // ✅ Gán Role lấy từ database
                .build();

        userRepository.save(userToSave);

        return UserResponse.builder()
                .username(userToSave.getUsername())
                .email(userToSave.getEmail())
                .phone(userToSave.getPhone())
                .name(userToSave.getName())
                .role(role.getRoleName()) // ✅ Lấy role từ DB, tránh lỗi detached entity
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

        // Lấy Role từ database thay vì gán trực tiếp từ pendingUser
        Role role = roleService.findByName(pendingUser.getRole() != null ? pendingUser.getRole().getRoleName() : "customer");
        if (role == null) {
            throw new RuntimeException("Role not found.");
        }

        // Lưu người dùng vào cơ sở dữ liệu
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
                .status(UserStatus.ACTIVATE) // Mặc định là ACTIVE
                .role(role) // ✅ Gán Role lấy từ database
                .build();

        userRepository.save(userToSave);

        // Xóa thông tin tạm thời sau khi đăng ký thành công
        otpService.clearPendingUser(email);

        return UserResponse.builder()
                .username(userToSave.getUsername())
                .email(userToSave.getEmail())
                .phone(userToSave.getPhone())
                .name(userToSave.getName())
                .role(role.getRoleName()) // ✅ Lấy role từ DB, tránh lỗi detached entity
                .build();
    }



    @Override
    public Map<String, Object> login(UserLoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.BLOCKED) {
            System.out.println("User Status: " + user.getStatus()); // Debug
            throw new AppException(ErrorCode.USER_BLOCKED);
        }


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateToken(request.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());

        // Chuẩn bị dữ liệu phản hồi
        UserLoginResponse userResponse = new UserLoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getImageUrl(),
                user.getCreatedAt(),
                user.getDescription(),
                user.getRole().getRoleName()
        );

// Tạo phản hồi chứa accessToken, refreshToken và thông tin user
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", userResponse);

        return response;
    }


    @Override
    public String refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        if (username != null || !jwtUtil.isTokenValid(refreshToken, username)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        return jwtUtil.generateToken(username);
    }

    @Override
    public String forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

        // Cập nhật thông tin người dùng nếu có dữ liệu mới
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
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
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .address(user.getAddress())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
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
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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


    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.BLOCKED); // Chỉ đánh dấu là đã "xóa"
        userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.DEACTIVATED); // Chuyển trạng thái thành DEACTIVATED
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.ACTIVATE); // Kích hoạt lại user
        userRepository.save(user);
    }

}