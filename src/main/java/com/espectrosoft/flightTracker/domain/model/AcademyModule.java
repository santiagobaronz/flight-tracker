package com.espectrosoft.flightTracker.domain.model;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "section", nullable = false, length = 20)
    @Builder.Default
    private ModuleSection section = ModuleSection.APPLICATION;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_code", nullable = false, length = 40)
    private ModuleCode moduleCode;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "name", length = 120)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "route", length = 255)
    private String route;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "academy_module_attributes", joinColumns = @JoinColumn(name = "academy_module_id"))
    @Column(name = "attribute", length = 100)
    @Builder.Default
    private List<String> attributes = new ArrayList<>();
}
