package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final OtpService otpService;

    private final RoleService roleService;

    private final JwtUtil jwtUtil;

    // Đăng ký tạm thời cho khách hàng
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

    // Đăng ký tạm thời cho nhân viên
    @Override
    public UserResponse registerStaff(UserRegisterRequest user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTED);
        }

        // Lấy role từ database thay vì gán trực tiếp từ request
        Role role = roleService.findByName(user.getRole() != null ? user.getRole().getRoleName() : "staff");
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

    // Tạo người dùng
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
                .role(role) // Gán Role lấy từ database
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


    // Xác thực OTP
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


    // Đăng nhập người dùng
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
                user.getGender(),
                user.getDateOfBirth(),
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
        String email = jwtUtil.extractUserMail(refreshToken);
        if (email != null || !jwtUtil.isTokenValid(refreshToken, email)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        return jwtUtil.generateToken(email);
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

    // Đổi mật khẻu
    @Override
    public String changePassword(Long userId, String oldPassword, String newPassword) {
        // Tìm kiếm người dùng theo ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Đổi mật khẩu thành công";
    }

    // Lấy tất cả danh sách
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // Lấy tất cả danh sách theo role customer
    @Override
    public List<User> getAllCustomers() {
        return userRepository.findAllByRoleRoleName("customer");
    }

    @Override
    public List<User> getAllAdmins() {
        return userRepository.findAllByRoleRoleName("admin");
    }

    @Override
    public List<User> getAllStaff() {
        return userRepository.findAllByRoleRoleName("staff");
    }

    @Override
    public List<User> getAllSuperAdmins() {
        return userRepository.findAllByRoleRoleName("superadmin");
    }

    // Update user
    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

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

        // Cập nhật gender nếu có
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            user.setGender(request.getGender());
        }

        // Cập nhật dateOfBirth nếu có
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
                .build();
    }


    // Lấy thông tin theo id
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        log.info("GetUserResponse: {}", id);
        return UserResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .imageUrl(user.getImageUrl())
                .description(user.getDescription())
                .role(user.getRole().getRoleName())
                .status(user.getStatus().name())
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


    @Override
    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.BLOCKED); // Chỉ đánh dấu là đã "xóa"
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.DEACTIVATED); // Chuyển trạng thái thành DEACTIVATED
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.ACTIVATE); // Kích hoạt lại user
        userRepository.save(user);
    }

    @Override
    public byte[] exportUsersToExcel() throws IOException {
        List<User> users = userRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Style cho header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);

        // Style cho data
        CellStyle dataStyle = workbook.createCellStyle();
        setBorder(dataStyle);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã khách hàng", "Email", "Họ tên khách hàng", "Số điện thoại", "Địa chỉ", "Ảnh URL", "Hội viên", "Trạng thái", "Ngày tạo", "Ngày cập nhật"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, user.getUserId(), dataStyle);
            createCell(row, 1, user.getEmail(), dataStyle);
            createCell(row, 2, user.getName(), dataStyle);
            createCell(row, 3, user.getGender() != null ? user.getGender() : "Trống", dataStyle);
            createCell(row, 4, user.getDateOfBirth() != null ? user.getDateOfBirth().format(dateFormatter) : "Trống", dataStyle);
            createCell(row, 5, user.getPhone(), dataStyle);
            createCell(row, 6, user.getAddress(), dataStyle);
            createCell(row, 7, user.getImageUrl(), dataStyle);
            createCell(row, 8, user.getDescription(), dataStyle);
            createCell(row, 9, user.getStatus().name(), dataStyle);
            createCell(row, 10, user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "", dataStyle);
            createCell(row, 11, user.getUpdatedAt() != null ? user.getUpdatedAt().format(formatter) : "", dataStyle);
        }

        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

}