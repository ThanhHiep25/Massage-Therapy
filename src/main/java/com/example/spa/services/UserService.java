package com.example.spa.services;


import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.request.UserRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponse register(UserRegisterRequest user);

    UserResponse createUser(UserRegisterRequest request);

    Map<String, Object> login(UserLoginRequest request);

    String refreshToken(String refreshToken);

    UserResponse getUserById(Long id);

    User findByUsernameOrEmail(String identifier);

    Page<User> getAllUsers(Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteUserById(Long id);

    String extractTokenFromCookies(Cookie[] cookies);

    UserResponse verifyOtp(String email, String otp);

    void logout(HttpServletResponse response);

    String forgotPassword(String email);

    String resetPassword(String email, String otp, String newPassword);

    String changePassword(Long userId, String oldPassword, String newPassword);

    List<User> getAllUsers();

    List<User> getAllCustomers();

    List<User> getAllAdmins();

    List<User> getAllSuperAdmins();

    UserResponse updateUser(Long id, UserRequest request);

    void blockUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    byte[] exportUsersToExcel() throws IOException;
}
