package com.example.spa.dto.request;

import com.example.spa.entities.Position;
import com.example.spa.entities.Staff;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class StaffRequest {

    @NotBlank(message = "Vui lòng nhập họ và tên.")
    @Pattern(regexp = "^[a-zA-Z\\u00C0-\\u1FFF\\s]+$", message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng.")
    private String name;

    @NotBlank(message = "Vui lòng nhập số điện thoại.")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số.")
    private String phone;

    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không hợp lệ.")
    private String email;

    @NotBlank(message = "Vui lòng nhập địa chỉ.")
    private String address;

    @NotNull(message = "Vui lòng chọn chức vụ.")
    private Long positionId;
    private String imageUrl;
    private String description;
    private String startDate;

    public Staff toPartialStaff(Position position){

        Staff staff = new Staff();
        staff.setName(name);
        staff.setPhone(phone);
        staff.setEmail(email);
        staff.setAddress(address);
        staff.setPosition(position); // Truyền vào đúng kiểu dữ liệu Position
        staff.setImageUrl(imageUrl);
        staff.setDescription(description);
        // Chuyển đổi String -> LocalDate
        if (startDate != null && !startDate.isEmpty()) {
            staff.setStartDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE));
        }
        return staff;
    }
}
