package com.example.spa.controllers;


import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.Role;
import com.example.spa.entities.User;
import com.example.spa.services.RoleService;
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
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    public ResultResponse<?> register(@Valid @RequestBody UserRegisterRequest user) {
        try {
            return ResultResponse.<UserResponse>builder().result(userService.register(user)).build();
        } catch (RuntimeException e) {
            return ResultResponse.builder().message(e.getMessage()).build();
        }
    }

    // Verify OTP
    @Operation(summary = "Xác thực OTP", description = "Trả về thông báo xác thực")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP hợp lệ"
            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    @PostMapping("/verify-otp")
    public ResultResponse<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            return ResultResponse.<UserResponse>builder()
                    .result(userService.verifyOtp(email, otp))
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

    public ResponseEntity<?> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
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
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
            response.addCookie(refreshTokenCookie);

            // Trả về toàn bộ dữ liệu bao gồm accessToken, refreshToken và thông tin user
            return ResponseEntity.ok(tokens);

        } catch (RuntimeException e) {
            // Xử lý lỗi đăng nhập
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid credentials",
                    "message", e.getMessage()
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
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token is missing"));
        }

        try {
            String newAccessToken = userService.refreshToken(refreshToken);
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
    @Operation(summary = "Reset mật khẩu", description = "Thay đ��i mật khẩu")
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
                .map(user -> new UserResponse((user)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Thông tin user", description = "Trả về thông tin user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResultResponse<?> getUser(@PathVariable Long id, HttpServletRequest request) {
        return ResultResponse.<GetUserResponse>builder().result(userService.getUserById(id)).build();
    }


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

    // Xóa mềm user
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

    // Xóa mềm user
    @DeleteMapping("block/{id}")
    @Operation(summary = "Xóa user", description = "Xóa user theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User đã bị đánh dấu là xóa.");
    }


    // Vô hiệu hóa user
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa user", description = "Vô hiệu hóa user theo id")
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

}