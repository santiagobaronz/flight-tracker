package com.espectrosoft.flightTracker.application.seed;

import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

@Configuration
@Profile("seed")
public class SeedConfig {

    @Bean
    public org.springframework.boot.CommandLineRunner seedRunner(
            AcademyRepository academyRepository,
            RoleRepository roleRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRepository userRepository,
            AircraftRepository aircraftRepository,
            AcademyModuleRepository academyModuleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            final Academy academy = academyRepository.findById(1L).orElseGet(() -> {
                final Academy a = Academy.builder()
                        .name("Espectro Academy")
                        .address("Default Address 123")
                        .phoneNumber("+57 3000000000")
                        .build();
                return academyRepository.save(a);
            });

            final Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").description("Administrador").build()));
            final Role instrRole = roleRepository.findByName("INSTRUCTOR").orElseGet(() -> roleRepository.save(Role.builder().name("INSTRUCTOR").description("Instructor").build()));
            final Role pilotRole = roleRepository.findByName("PILOT").orElseGet(() -> roleRepository.save(Role.builder().name("PILOT").description("Piloto").build()));

            ensurePermissions(rolePermissionRepository, adminRole, EnumSet.allOf(PermissionAction.class));
            ensurePermissions(rolePermissionRepository, instrRole, EnumSet.of(PermissionAction.VIEW, PermissionAction.EDIT));
            ensurePermissions(rolePermissionRepository, pilotRole, EnumSet.of(PermissionAction.VIEW));

            final User admin = userRepository.findByUsername("admin").orElseGet(() -> {
                final User u = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("Admin User")
                        .academy(academy)
                        .build();
                u.getRoles().add(adminRole);
                return userRepository.save(u);
            });

            userRepository.findByUsername("instr1").orElseGet(() -> {
                final User u = User.builder()
                        .username("instr1")
                        .password(passwordEncoder.encode("instr123"))
                        .fullName("Instructor Uno")
                        .academy(academy)
                        .build();
                u.getRoles().add(instrRole);
                return userRepository.save(u);
            });

            final User pilot = userRepository.findByUsername("pilot1").orElseGet(() -> {
                final User u = User.builder()
                        .username("pilot1")
                        .password(passwordEncoder.encode("pilot123"))
                        .fullName("Piloto Uno")
                        .academy(academy)
                        .build();
                u.getRoles().add(pilotRole);
                return userRepository.save(u);
            });

            aircraftRepository.findByAcademyAndTailNumber(academy, "HK-100").orElseGet(() ->
                    aircraftRepository.save(Aircraft.builder().academy(academy).tailNumber("HK-100").model("C172").type("SEL").build())
            );
            aircraftRepository.findByAcademyAndTailNumber(academy, "HK-200").orElseGet(() ->
                    aircraftRepository.save(Aircraft.builder().academy(academy).tailNumber("HK-200").model("C310").type("MEL").build())
            );

            academyModuleRepository.findByAcademyAndModuleCode(academy, ModuleCode.HOURS)
                    .orElseGet(() -> academyModuleRepository.save(AcademyModule.builder().academy(academy).moduleCode(ModuleCode.HOURS).active(true).build()));
        };
    }

    private void ensurePermissions(RolePermissionRepository repo, Role role, Set<PermissionAction> actions) {
        for (PermissionAction action : actions) {
            final RolePermission rp = RolePermission.builder()
                    .role(role)
                    .moduleCode(ModuleCode.HOURS)
                    .action(action)
                    .build();
            repo.save(rp);
        }
    }
}
