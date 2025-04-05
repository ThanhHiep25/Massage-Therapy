package com.example.spa.dto.request;

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
    private Long serviceId;
    private LocalDate assignedDate;
    private String note;
    private StaffServiceStatus status = StaffServiceStatus.Unassigned; // "Pending", "Completed", "Cancelled"


    @Override
    public String toString() {
        return "StaffServiceRequest{" +
                "staffId=" + staffId +
                ", serviceId=" + serviceId +
                ", assignedDate=" + assignedDate +
                ", note='" + note + '\'' +
                ", status=" + status +
                '}';
    }
}
