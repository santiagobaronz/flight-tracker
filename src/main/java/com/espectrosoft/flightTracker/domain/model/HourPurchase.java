package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hour_purchases",
        uniqueConstraints = @UniqueConstraint(columnNames = {"receipt_number", "aircraft_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pilot_id")
    private User pilot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "receipt_number", nullable = false, length = 60)
    private String receiptNumber;

    @Column(nullable = false)
    private double hours;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
