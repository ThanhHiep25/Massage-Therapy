package com.example.spa.dto.request;

import com.example.spa.enums.AppointmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentRequest {
    private Long userId;
    private String guestName;
    private LocalDateTime appointmentDateTime;
    private BigDecimal totalPrice;
    private String notes;
    private AppointmentStatus status ;
    private List<Long> serviceIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "userId=" + userId +
                ", guestName='" + guestName + '\'' +
                ", appointmentDateTime=" + appointmentDateTime +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                ", serviceIds=" + serviceIds +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
