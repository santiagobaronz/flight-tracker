package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hour_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @ManyToOne(optional = false)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Column(nullable = false)
    private double hours;

    @Column(name = "flight_date", nullable = false)
    private LocalDate flightDate;

    @Column(name = "logbook_number", length = 60, nullable = false)
    private String logbookNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
