package com.example.spa.entities;


import com.example.spa.enums.StatusBasic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @JsonIgnore
    private Position position;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @ManyToOne
    @JsonIgnore
    private Department department;

    @ManyToMany
    @JoinTable(
            name = "staff_service",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceSpa> services = new ArrayList<>();


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusBasic status = StatusBasic.ACTIVATE; // "Active", "Inactive", v.v.


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
                '}';
    }
}

