package com.example.spa.entities;

import com.example.spa.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_user_id",referencedColumnName = "userId", nullable = true)
    @JsonIgnore
    private User user;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "appointment_date_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime appointmentDateTime;

    @Column(name = "total_price", nullable = false, precision = 20, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "notes", columnDefinition = "TEXT") // Chấp nhận văn bản dài
    private String notes;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status; // Dùng Enum thay vì String

    // CHỈNH SỬA: Cho phép đặt nhiều dịch vụ trong một lịch hẹn
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "appointment_services",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @JsonIgnore
    private List<ServiceSpa> services;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", user=" + user +
                ", appointmentDateTime=" + appointmentDateTime +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                ", payment=" + payment +
                ", status=" + status +
                ", services=" + services +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
