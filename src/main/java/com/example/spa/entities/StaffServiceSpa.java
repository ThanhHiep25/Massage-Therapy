package com.example.spa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "staff_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffServiceSpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceSpa serviceSpa;

    @Column(name = "assigned_date")
    private LocalDate assignedDate; // Ngày nhân viên được giao dịch vụ

    @Override
    public String toString() {
        return "StaffService{" +
                "id=" + id +
                ", staff=" + staff.getName() +
                ", service=" + serviceSpa.getName() +
                ", assignedDate=" + assignedDate +
                '}';
    }
}
