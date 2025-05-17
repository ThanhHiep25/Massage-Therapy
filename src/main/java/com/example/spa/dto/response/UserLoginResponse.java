package com.example.spa.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String description;
    private String roles;


    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", role='" + roles + '\'' +
                '}';
    }
}