package com.example.spa.dto.response;

import com.example.spa.entities.Appointment;
import com.example.spa.enums.AppointmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AppointmentResponse {
    private Long id;
    private UserResponse userId;
    private String gustName;
    private LocalDateTime appointmentDateTime;
    private BigDecimal totalPrice;
    private String notes;
    private AppointmentStatus status;
    private List<ServiceSpaResponse> serviceIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentResponse(Appointment appointment) {
        this.id = appointment.getAppointmentId();
        this.userId = appointment.getUser() != null ? new UserResponse(appointment.getUser()) : null;
        this.gustName = appointment.getGuestName();
        this.appointmentDateTime = appointment.getAppointmentDateTime();
        this.totalPrice = appointment.getTotalPrice();
        this.notes = appointment.getNotes();
        this.status = appointment.getStatus();
        this.createdAt = appointment.getCreatedAt();
        this.updatedAt = appointment.getUpdatedAt();
        this.serviceIds = appointment.getServices().stream()
                .map(service -> new ServiceSpaResponse(service, service.getSteps()))
                .collect(Collectors.toList());
    }

}
