package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_principal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemPrincipal {
    @Id
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
