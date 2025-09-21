package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "academies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Academy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 50)
    private String phoneNumber;

    @Column
    private String logo;

    @Column
    private String icon;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "academy")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "academy")
    @Builder.Default
    private Set<Aircraft> aircrafts = new HashSet<>();
}
