package com.example.spa.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_steps")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long step_id;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceSpa serviceSpa;

    @Override
    public String toString() {
        return "ServiceStep{" +
                "step_id=" + step_id +
                ", stepOrder=" + stepOrder +
                ", description='" + description + '\'' +
                '}';
    }
}
