package com.example.spa.entities;

import com.example.spa.enums.StaffServiceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceSpa serviceSpa;

    @Column(name = "assigned_date")
    private LocalDate assignedDate; // Ngày nhân viên được giao dịch vụ

    @Column(name = "note")
    private String note;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StaffServiceStatus status; // "Pending", "Completed", "Cancelled"

    @Override
    public String toString() {
        return "StaffService{" +
                "id=" + id +
                ", staff=" + staff.getName() +
                ", service=" + serviceSpa.getName() +
                ", assignedDate=" + assignedDate +
                ", note='" + note + '\'' +
                ", status=" + status +
                '}';
    }
}
