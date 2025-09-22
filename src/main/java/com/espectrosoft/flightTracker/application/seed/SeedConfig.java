package com.espectrosoft.flightTracker.application.seed;

import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
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
            HourPurchaseRepository hourPurchaseRepository,
            HourUsageRepository hourUsageRepository,
            UserAircraftBalanceRepository balanceRepository,
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

            final Role adminRole = roleRepository.findByName("ADMINISTRATOR").orElseGet(() -> roleRepository.save(Role.builder().name("ADMINISTRATOR").description("Administrators").build()));
            final Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseGet(() -> roleRepository.save(Role.builder().name("EMPLOYEE").description("Employees").build()));
            final Role instrRole = roleRepository.findByName("INSTRUCTOR").orElseGet(() -> roleRepository.save(Role.builder().name("INSTRUCTOR").description("Instructors").build()));
            final Role clientRole = roleRepository.findByName("CLIENT").orElseGet(() -> roleRepository.save(Role.builder().name("CLIENT").description("Clients").build()));

            // ADMINISTRATOR: HOURS [VIEW, CREATE, EDIT, DELETE]; AIRCRAFT [VIEW, CREATE, EDIT, DELETE]
            ensurePermissions(rolePermissionRepository, adminRole, ModuleCode.HOURS, EnumSet.of(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.DELETE));
            ensurePermissions(rolePermissionRepository, adminRole, ModuleCode.AIRCRAFT, EnumSet.of(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT, PermissionAction.DELETE));

            // EMPLOYEE: HOURS [VIEW, CREATE, EDIT]; AIRCRAFT [VIEW]
            ensurePermissions(rolePermissionRepository, employeeRole, ModuleCode.HOURS, EnumSet.of(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT));
            ensurePermissions(rolePermissionRepository, employeeRole, ModuleCode.AIRCRAFT, EnumSet.of(PermissionAction.VIEW));

            // INSTRUCTOR: HOURS [VIEW, CREATE, EDIT]
            ensurePermissions(rolePermissionRepository, instrRole, ModuleCode.HOURS, EnumSet.of(PermissionAction.VIEW, PermissionAction.CREATE, PermissionAction.EDIT));

            // CLIENT: HOURS [VIEW]
            ensurePermissions(rolePermissionRepository, clientRole, ModuleCode.HOURS, EnumSet.of(PermissionAction.VIEW));

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

            userRepository.findByUsername("employee1").orElseGet(() -> {
                final User u = User.builder()
                        .username("employee1")
                        .password(passwordEncoder.encode("employee123"))
                        .fullName("Employee Uno")
                        .academy(academy)
                        .build();
                u.getRoles().add(employeeRole);
                return userRepository.save(u);
            });

            userRepository.findByUsername("client1").orElseGet(() -> {
                final User u = User.builder()
                        .username("client1")
                        .password(passwordEncoder.encode("client123"))
                        .fullName("Cliente Uno")
                        .academy(academy)
                        .build();
                u.getRoles().add(clientRole);
                return userRepository.save(u);
            });

            aircraftRepository.findByAcademyAndRegistration(academy, "HK-100").orElseGet(() ->
                    aircraftRepository.save(Aircraft.builder().academy(academy).registration("HK-100").model("C172").type(AircraftType.AIRCRAFT).build())
            );
            aircraftRepository.findByAcademyAndRegistration(academy, "HK-200").orElseGet(() ->
                    aircraftRepository.save(Aircraft.builder().academy(academy).registration("HK-200").model("C310").type(AircraftType.AIRCRAFT).build())
            );

            academyModuleRepository.findByAcademyAndModuleCode(academy, ModuleCode.HOURS)
                    .orElseGet(() -> academyModuleRepository.save(AcademyModule.builder().academy(academy).moduleCode(ModuleCode.HOURS).active(true).build()));

            academyModuleRepository.findByAcademyAndModuleCode(academy, ModuleCode.AIRCRAFT)
                    .orElseGet(() -> academyModuleRepository.save(AcademyModule.builder().academy(academy).moduleCode(ModuleCode.AIRCRAFT).active(true).build()));

            // Seed APPLICATION section entries (best-effort: update names and routes)
            academyModuleRepository.findByAcademyAndModuleCode(academy, ModuleCode.HOURS)
                    .ifPresent(m -> { m.setName("Horas"); m.setDescription("Gestión de horas"); m.setRoute("/app/hours"); academyModuleRepository.save(m);} );
            academyModuleRepository.findByAcademyAndModuleCode(academy, ModuleCode.AIRCRAFT)
                    .ifPresent(m -> { m.setName("Aeronaves"); m.setDescription("Listado de aeronaves"); m.setRoute("/app/aircraft"); academyModuleRepository.save(m);} );

            // Create sample Hour Purchase and update balance
            final User client = userRepository.findByUsername("client1").orElseThrow();
            final User instructor = userRepository.findByUsername("instr1").orElseThrow();
            final User adminUser = userRepository.findByUsername("admin").orElseThrow();
            final Aircraft ac = aircraftRepository.findByAcademyAndRegistration(academy, "HK-100").orElseThrow();

            if (!hourPurchaseRepository.existsByReceiptNumberAndAircraft("R-SEED-1", ac)) {
                final HourPurchase hp = HourPurchase.builder()
                        .academy(academy)
                        .client(client)
                        .aircraft(ac)
                        .receiptNumber("R-SEED-1")
                        .hours(5.0)
                        .purchaseDate(LocalDate.now())
                        .createdBy(adminUser)
                        .build();
                hourPurchaseRepository.save(hp);

                final UserAircraftBalance bal = balanceRepository.findByClientAndAircraft(client, ac)
                        .orElseGet(() -> UserAircraftBalance.builder()
                                .client(client)
                                .aircraft(ac)
                                .totalPurchased(0)
                                .totalUsed(0)
                                .balanceHours(0)
                                .build());
                bal.setTotalPurchased(bal.getTotalPurchased() + 5.0);
                bal.setBalanceHours(bal.getBalanceHours() + 5.0);
                balanceRepository.save(bal);
            }

            // Create sample Hour Usage and update balance
            final boolean existsUsage = hourUsageRepository.count() > 0;
            if (!existsUsage) {
                final UserAircraftBalance bal2 = balanceRepository.findByClientAndAircraft(client, ac)
                        .orElseGet(() -> UserAircraftBalance.builder()
                                .client(client)
                                .aircraft(ac)
                                .totalPurchased(0)
                                .totalUsed(0)
                                .balanceHours(0)
                                .build());
                double use = Math.min(2.0, bal2.getBalanceHours() + 5.0); // best-effort
                final HourUsage hu = HourUsage.builder()
                        .academy(academy)
                        .client(client)
                        .aircraft(ac)
                        .instructor(instructor)
                        .hours(use)
                        .flightDate(LocalDate.now())
                        .logbookNumber("L-SEED-1")
                        .createdBy(adminUser)
                        .build();
                hourUsageRepository.save(hu);
                bal2.setTotalUsed(bal2.getTotalUsed() + use);
                bal2.setBalanceHours(bal2.getBalanceHours() - use);
                balanceRepository.save(bal2);
            }
        };
    }

    private void ensurePermissions(RolePermissionRepository repo, Role role, ModuleCode module, Set<PermissionAction> actions) {
        for (PermissionAction action : actions) {
            final RolePermission rp = RolePermission.builder()
                    .role(role)
                    .moduleCode(module)
                    .action(action)
                    .build();
            repo.save(rp);
        }
    }
}
