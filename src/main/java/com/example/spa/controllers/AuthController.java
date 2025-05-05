package com.example.spa.controllers;


import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.OtpRequest;
import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.exception.AppException;
import com.example.spa.services.UserService;
import com.example.spa.servicesImpl.OtpService;
import com.example.spa.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;


    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;


    // Register user with the user service
    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Trả về thông tin tài khoản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thông tin đăng ký hợp lệ"
            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest user) {
        return ResponseEntity.ok(userService.register(user));
    }

    // Verify OTP
    @Operation(summary = "Xác thực OTP", description = "Trả về thông báo xác thực")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP hợp lệ"
            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    @PostMapping("/verify-otp")
    public ResultResponse<?> verifyOtp(@RequestBody OtpRequest request) {
        try {
            return ResultResponse.<UserResponse>builder()
                    .result(userService.verifyOtp(request.getEmail(), request.getOtp()))
                    .message("OTP verified successfully")
                    .build();
        } catch (RuntimeException e) {
            return ResultResponse.builder().message(e.getMessage()).build();
        }
    }

    // Create user
    @PostMapping("/create")
    @Operation(summary = "Tạo user", description = "Tạo user mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"
            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRegisterRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    // Login request
    @PostMapping("/login")
    @Operation(summary = "Đăng nhập tài khoản", description = "Trả về thông tin đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thông tin đăng nhập hợp lệ"
            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })

    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request, HttpServletResponse response) {
        try {
            // Gọi service để xử lý đăng nhập
            Map<String, Object> tokens = userService.login(request);

            // Lấy refreshToken từ response map
            String refreshToken = (String) tokens.get("refreshToken");

            // Tạo HTTP-only cookie chứa Refresh Token
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");

            // set refesh token 7 ngày
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
            response.addCookie(refreshTokenCookie);

            // Trả về toàn bộ dữ liệu bao gồm accessToken, refreshToken và thông tin user
            return ResponseEntity.ok(tokens);

        } catch (RuntimeException e) {
            // Xử lý lỗi đăng nhập
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid credentials",
                    "message", e.getMessage(),
                    "code", e.hashCode()
            ));
        }
    }


    // Refresh token request
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Trả về thông tin refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        // Kiem tra xem refreshToken co ton tai khong
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token is missing"));
        }

        try {
            // Goi service de refresh token
            String newAccessToken = userService.refreshToken(refreshToken);
            // Tao cookie moi
            response.addCookie(new Cookie("refreshToken", refreshToken));
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    //Forgot password
    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu", description = "Gửi OTP qua email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResultResponse<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String otp = otpService.generateOtp();
            otpService.sendOtpEmail(request.get("email"), otp);
            return ResultResponse.builder().message("OTP đã được gửi tới email của bạn").build();
        } catch (RuntimeException e) {
            return ResultResponse.builder().message(e.getMessage()).build();
        }
    }

    //Reset password
    @PostMapping("/reset-password")
    @Operation(summary = "Reset mật khẩu", description = "Thay đổi mật khẩu")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")})
    public ResultResponse<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        try {
            if (email == null || otp == null || newPassword == null) {
                return ResultResponse.builder().message("Invalid request").build();
            }
            return ResultResponse.builder().message(userService.resetPassword(email, otp, newPassword)).build();
        } catch (RuntimeException e) {
            return ResultResponse.builder().message(e.getMessage()).build();
        }
    }

    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu bằng cách nhập mật khẩu cũ và mật khẩu mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu cũ không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Người dùng không tồn tại")
    })
    public ResponseEntity<String> changePassword(@RequestParam Long userId,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        try {
            String response = userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Get all users
    @GetMapping("/all")
    @Operation(summary = "Danh sách tất cả user", description = "Trả về danh sách tất cả user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })  // This method returns a list of UserResponse objects.
    public ResponseEntity<List<UserResponse>> getAllusers() {
        List<UserResponse> responses = userService.getAllUsers()
                .stream()
                .map(UserResponse::new)
//                .map(user -> new UserResponse((user)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Get all customers
    @GetMapping("/customers")
    @Operation(summary = "Danh sách tất cả user theo role customer", description = "Trả về danh sách tất cả user theo role customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        List<UserResponse> responses = userService.getAllCustomers()
                .stream()
                .map(UserResponse::new)
//                .map(user -> new UserResponse((user)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Get all admin
    @GetMapping("/admins")
    @Operation(summary = "Danh sách tất cả user theo role admin", description = "Trả về danh sách tất cả user theo role admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<List<UserResponse>> getAllAdmin() {
        List<UserResponse> responses = userService.getAllAdmins()
                .stream()
                .map(UserResponse::new)
//                .map(user -> new UserResponse((user)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }


    // Thông tin user theo id
    @GetMapping("/{id}")
    @Operation(summary = "Thông tin user", description = "Trả về thông tin user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    // Cập nhật thông tin user
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật user", description = "Cập nhật thông tin user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // Đăng xuất
    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Xóa refreshToken trong cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> logout(HttpServletResponse response) {
        userService.logout(response);
        return ResponseEntity.ok().body(ResultResponse.builder().message("Logged out successfully").build());
    }

    // Xóa hẳng  user
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa user", description = "Xóa user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User đã bị đánh dấu là xóa.");
    }

    // khóa tài khoản user mềm user
    @PutMapping("/{id}/block")
    @Operation(summary = "Khóa tài khoản user", description = "Khóa tài khoản user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok("User đã bị đánh dấu là xóa.");
    }


    // Ngưng hoạt động tài khoản
    @PutMapping("/{id}/deactivated")
    @Operation(summary = "Ngưng hoạt động tài khoản", description = "Ngưng hoạt động tài khoản user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User đã bị vô hiệu hóa.");
    }

    // Kích hoạt lại user
    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt user", description = "Kích hoạt user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User đã được kích hoạt lại.");
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Xuat danh sach khach hang thanh file excel", description = "Xuat danh sach khach hang thanh file excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<byte[]> exportUsersToExcel() throws IOException {
        byte[] excelBytes = userService.exportUsersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        // Tạo tên file với ngày và giờ xuất
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "DanhSachKhachHang_" + formatter.format(now) + ".xlsx";

        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

}