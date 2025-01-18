package com.example.spa.controllers;


import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.Role;
import com.example.spa.services.RoleService;
import com.example.spa.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    // Register user with the user service
    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Trả về thông tin tài khoản")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Thông tin đăng ký hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResultResponse<?> register(@Valid @RequestBody UserRegisterRequest user) {
        //return ResultResponse.<UserResponse>builder().result(userService.register(user)).build();

        try {
            if (userService.existsByUsername(user.getUsername())) {
                return ResultResponse.builder().message("Username already exists!").build();
            }
            if (userService.existsByEmail(user.getEmail())) {
                return ResultResponse.builder().message("Email already exists!").build();
            }

            Role role = roleService.findByName("superadmin");
            user.setRole(role);

            return ResultResponse.<UserResponse>builder().result(userService.register(user)).build();
        }catch (RuntimeException e){
            return ResultResponse.builder().message(e.getMessage()).build();
        }
    }

    // Login request
    @PostMapping("/login")
    @Operation(summary = "Đăng nhập tài khoản", description = "Trả về thông tin đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Thông tin đăng nhập hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        try {
            // Trả về cả Access Token và Refresh Token
            var tokens = userService.login(request);
            Cookie refreshTokenCookie = createHttpOnlyCookie("refreshToken", tokens.get("refreshToken"));
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
//        try {
//            String newAccessToken = userService.refreshToken(refreshToken);
//            return ResponseEntity.ok("accessToken:" + newAccessToken);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    // Refresh token request
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Trả về thông tin refresh token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token is missing"));
        }

        try {
            // Làm mới Access Token từ Refresh Token
            String newAccessToken = userService.refreshToken(refreshToken);

            // Cập nhật lại cookie nếu cần
            response.addCookie(createHttpOnlyCookie("refreshToken", refreshToken));

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/users/{id}")
    @Operation(summary = "Thông tin user", description = "Trả về thông tin user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Thông tin hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResultResponse<?> getUser(@PathVariable Long id) {
        return com.example.spa.dto.ResultResponse.<GetUserResponse>builder().result(userService.getUserById(id)).build();
    }

    private Cookie createHttpOnlyCookie(String name, String value) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(name, value);
        cookie.setHttpOnly(true);  // Chỉ được truy cập từ HTTP, không phải JavaScript
        cookie.setSecure(true);    // Chỉ gửi cookie qua HTTPS
        cookie.setPath("/");       // Áp dụng cho toàn bộ ứng dụng
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        //cookie.setMaxAge(60); //60s
        return cookie;
    }

    public class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

//    // Class to represent OTP request payload
//    public static class OtpRequest {
//        private String otp;
//
//        public String getOtp() {
//            return otp;
//        }
//
//        public void setOtp(String otp) {
//            this.otp = otp;
//        }
//    }

}