package com.example.spa.dto.request;

import com.example.spa.entities.Role;
import com.example.spa.entities.User;
import com.example.spa.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRequest {
    private String username;
    private String email;
    private String name;
    private String password;
    private String address;
    private String description;
    private String phone;
    private String imageUrl;
    private Role role;

    public User toPartialUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setAddress(this.address);
        user.setDescription(this.description);
        user.setImageUrl(this.imageUrl);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", phone='" + phone + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", role=" + role +
                '}';
    }
}
