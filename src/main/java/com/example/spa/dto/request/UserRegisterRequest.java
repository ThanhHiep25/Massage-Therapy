package com.example.spa.dto.request;

import com.example.spa.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {
    private String username;


    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @NotBlank(message = "Tên đầy đủ không được để trống")
    @Size(min = 2, max = 100, message = "Tên đầy đủ phải từ 2 đến 100 ký tự")
    private String name;

    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$",
            message = "Password phải chứa ít nhất 1 chữ số, 1 chữ thường, 1 chữ hoa, 1 ký tự đặc biệt và không có khoảng trắng")
    private String password;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
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
