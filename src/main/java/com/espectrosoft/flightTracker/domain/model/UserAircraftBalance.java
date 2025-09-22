package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_aircraft_balances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "aircraft_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAircraftBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "total_purchased", nullable = false)
    private double totalPurchased;

    @Column(name = "total_used", nullable = false)
    private double totalUsed;

    @Column(name = "balance_hours", nullable = false)
    private double balanceHours;
}
