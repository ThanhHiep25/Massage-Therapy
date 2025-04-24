package com.example.spa.dto.request;

import com.example.spa.entities.Role;
import com.example.spa.entities.User;
import com.example.spa.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRequest {
    private String email;
    private String name;
    private String password;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String description;
    private String phone;
    private String imageUrl;


    public User toPartialUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setAddress(this.address);
        user.setName(this.name);
        user.setGender(this.gender);
        user.setDateOfBirth(this.dateOfBirth);
        user.setDescription(this.description);
        user.setImageUrl(this.imageUrl);
        return user;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", description='" + description + '\'' +
                ", phone='" + phone + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
