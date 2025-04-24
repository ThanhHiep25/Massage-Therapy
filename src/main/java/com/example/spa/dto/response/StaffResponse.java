package com.example.spa.dto.response;

import com.example.spa.entities.Position;
import com.example.spa.entities.Staff;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponse {

    private Long staffId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Position position;
    //private String position;
    private String imageUrl;
    private String description;
    private String startDate;
    private String status;

    public StaffResponse(Staff updateStaff) {
        this.staffId = updateStaff.getStaffId();
        this.name = updateStaff.getName();
        this.phone = updateStaff.getPhone();
        this.email = updateStaff.getEmail();
        this.address = updateStaff.getAddress();
        this.position = updateStaff.getPosition();
        //this.position = updateStaff.getPosition().getPositionName();
        this.imageUrl = updateStaff.getImageUrl();
        this.description = updateStaff.getDescription();
        this.startDate = updateStaff.getStartDate().toString();
        this.status = updateStaff.getStatus().name();
    }

}
