package com.example.spa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "service_spa_service_id")
    private ServiceSpa serviceSpa;

    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;


    @Column(name = "notes")
    private String notes;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Payment payment;



    @Column(name = "status", nullable = false)
    private String status; // Example: "Scheduled", "Completed", "Cancelled"


}
