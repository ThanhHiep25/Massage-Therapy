package com.example.spa.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "staffs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long staffId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Position position;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "status")
    private String status; // "Active", "Inactive", v.v.

    @Column(name = "customers_served")
    private int customersServed;

    @Column(name = "total_revenue")
    private double totalRevenue;

    @ManyToOne (cascade = CascadeType.PERSIST)
    @JoinColumn(name = "servicespa_id")
    private ServiceSpa serviceSpa;

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", position=" + position +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", status='" + status + '\'' +
                ", customersServed=" + customersServed +
                ", totalRevenue=" + totalRevenue +
                ", serviceSpa=" + serviceSpa +
                '}';
    }
}

