package com.example.spa.dto.response;

import com.example.spa.entities.User;
import lombok.*;

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
    private String imageUrl;
    private String description;
    private String role;
    private String status;

    public UserResponse(User updateUser) {
        this.id = updateUser.getUserId();
        this.username = updateUser.getUsername();
        this.email = updateUser.getEmail();
        this.phone = updateUser.getPhone();
        this.name = updateUser.getName();
        this.address = updateUser.getAddress();
        this.imageUrl = updateUser.getImageUrl();
        this.description = updateUser.getDescription();
        this.role = updateUser.getRole().getRoleName();
        this.status = updateUser.getStatus().name();

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
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
