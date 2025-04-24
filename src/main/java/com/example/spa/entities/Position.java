package com.example.spa.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "positions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long positionId;

    @Column(name = "position_name", nullable = false, unique = true)
    private String positionName;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "Position{" +
                "positionId=" + positionId +
                ", positionName='" + positionName + '\'' +
                ", description='" + description + '\'' +

                '}';
    }
}
