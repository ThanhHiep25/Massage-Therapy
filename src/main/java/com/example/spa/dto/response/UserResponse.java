package com.example.spa.dto.response;

import com.example.spa.entities.User;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String name;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String imageUrl;
    private String description;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse(User updateUser) {
        this.id = updateUser.getUserId();
        this.username = updateUser.getUsername();
        this.email = updateUser.getEmail();
        this.phone = updateUser.getPhone();
        this.name = updateUser.getName();
        this.address = updateUser.getAddress();
        this.gender = updateUser.getGender();
        this.dateOfBirth = updateUser.getDateOfBirth();
        this.imageUrl = updateUser.getImageUrl();
        this.description = updateUser.getDescription();
        this.role = updateUser.getRole().getRoleName();
        this.status = updateUser.getStatus().name();
        this.createdAt = updateUser.getCreatedAt();
        this.updatedAt = updateUser.getUpdatedAt();

    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
