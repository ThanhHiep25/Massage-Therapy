package com.example.spa.dto.request;

import com.example.spa.entities.Position;
import com.example.spa.entities.Staff;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class StaffRequest {
    private String name;
    private String phone;
    private String email;
    private String address;
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
