package com.example.spa.dto.request;

import com.example.spa.entities.Department;
import com.example.spa.enums.StaffServiceStatus;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StaffServiceRequest {
    private Long staffId;
    private Long appointmentId;
    private Long departmentId;
    private LocalDate assignedDate;
    private String note;
    private StaffServiceStatus status = StaffServiceStatus.Unassigned; // "Pending", "Completed", "Cancelled"


    @Override
    public String toString() {
        return "StaffServiceRequest{" +
                "staffId=" + staffId +
                ", department=" + departmentId +
                ", serviceId=" + appointmentId +
                ", assignedDate=" + assignedDate +
                ", note='" + note + '\'' +
                ", status=" + status +
                '}';
    }
}
