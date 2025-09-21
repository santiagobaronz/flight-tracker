package com.espectrosoft.flightTracker.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import com.espectrosoft.flightTracker.domain.model.enums.UserAttributeKey;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false, length = 120)
    private String fullName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ElementCollection
    @CollectionTable(name = "user_attributes", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "attr_key", length = 50)
    @Column(name = "attr_value", length = 255)
    @Builder.Default
    private Map<UserAttributeKey, String> attributes = new HashMap<>();
}
