package com.example.spa.servicesImpl;

import com.example.spa.dto.request.UserLoginRequest;
import com.example.spa.dto.request.UserRegisterRequest;
import com.example.spa.dto.response.GetUserResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.User;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.UserRepository;
import com.example.spa.services.UserService;
import com.example.spa.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private JwtUtil jwtUtil;

    @Override
    public UserResponse register(UserRegisterRequest user) {

        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new RuntimeException("Username already exists!");
            throw new AppException(ErrorCode.INVALID_DOB);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        User useSave = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .password(passwordEncoder.encode(user.getPassword()))
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole())
                .build();
        userRepository.save(useSave);
        return new UserResponse().builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .role(user.getRole().getRoleName())
                .build();
    }
    

//    @Override
//    public String login(UserLoginRequest request) {
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid username or password!");
//        }
//
//        return jwtUtil.generateToken(request.getUsername());
//    }

    @Override
    public Map<String, String> login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @Override
    public String refreshToken(String refreshToken){
        String username = jwtUtil.extractUsername(refreshToken);
        if (username != null || !jwtUtil.isTokenValid(refreshToken, username)){
            throw new RuntimeException("Invalid refresh token");
        }
        return jwtUtil.generateToken(username);
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
}