package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aircrafts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"academy_id", "tail_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @Column(name = "tail_number", nullable = false, length = 20)
    private String tailNumber;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(nullable = false, length = 80)
    private String type;
}
