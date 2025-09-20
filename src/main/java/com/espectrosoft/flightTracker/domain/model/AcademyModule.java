package com.espectrosoft.flightTracker.domain.model;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academy_modules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"academy_id", "module_code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademyModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_code", nullable = false, length = 40)
    private ModuleCode moduleCode;

    @Column(name = "active", nullable = false)
    private boolean active;
}
