package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;

@Entity
@Table(name = "aircrafts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"academy_id", "registration"}))
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

    @Column(name = "registration", nullable = false, length = 20)
    private String registration;

    @Column(nullable = false, length = 80)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AircraftType type;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
