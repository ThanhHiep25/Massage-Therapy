package com.example.spa.dto.request;

import com.example.spa.entities.Role;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    @Size(message = "User register request", min = 4, max = 100)
    private String username;
    private String email;
    private String name;
    private String password;
    private String address;
    private String description;
    private String phone;
    private String imageUrl;
    private Role role;

    public LocalDateTime getCreatedAt() {
        return LocalDateTime.now();
    }


    public LocalDateTime getUpdatedAt() {
        return LocalDateTime.now();
    }
}
