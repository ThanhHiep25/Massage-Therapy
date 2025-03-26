package com.example.spa.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_spa_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceSpaImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_spa_id", nullable = false)
    private ServiceSpa serviceSpa;

    private String imageUrl;

    @Override
    public String toString() {
        return "ServiceSpaImage{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
