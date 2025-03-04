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
    private Long serviceSpaId;
    private LocalDateTime appointmentDateTime;
    private Long staffId;
    private BigDecimal totalPrice;
    private String notes;
    private AppointmentStatus status;
    private List<Long> serviceIds;

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "userId=" + userId +
                ", serviceSpaId=" + serviceSpaId +
                ", appointmentDateTime=" + appointmentDateTime +
                ", staffId=" + staffId +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                ", serviceIds=" + serviceIds +
                '}';
    }
}
