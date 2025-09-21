package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.exception.BusinessException;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.core.policy.AccessValidationUseCase;
import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUsageUseCaseImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private HourUsageRepository hourUsageRepository;
    @Mock
    private UserAircraftBalanceRepository balanceRepository;
    @Mock
    private AccessValidationUseCase accessValidationUseCase;
    @Mock
    private UserActivePolicy userActivePolicy;

    private RegisterUsageUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUsageUseCaseImpl(
                academyRepository,
                userRepository,
                aircraftRepository,
                hourUsageRepository,
                balanceRepository,
                accessValidationUseCase,
                userActivePolicy
        );
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("admin", ""));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void register_ok_updates_balance_and_persists() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(10L).username("p1").academy(academy).fullName("P").password("x").build();
        final User instructor = User.builder().id(11L).username("i1").academy(academy).fullName("I").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).tailNumber("HK-1").model("M").type("SEL").build();
        final User creator = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final UserAircraftBalance balance = UserAircraftBalance.builder()
                .pilot(pilot).aircraft(aircraft).totalPurchased(5).totalUsed(1).balanceHours(4).build();
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(10L);
        req.setInstructorId(11L);
        req.setAircraftId(100L);
        req.setHours(2.0);
        req.setFlightDate(LocalDate.now());
        req.setLogbookNumber("L-1");

        when(academyRepository.findById(1L)).thenReturn(Optional.of(academy));
        doNothing().when(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        when(userRepository.findById(10L)).thenReturn(Optional.of(pilot));
        when(userRepository.findById(11L)).thenReturn(Optional.of(instructor));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(balanceRepository.findByPilotAndAircraft(pilot, aircraft)).thenReturn(Optional.of(balance));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(creator));
        doNothing().when(userActivePolicy).apply(creator);
        when(hourUsageRepository.save(any(HourUsage.class))).thenAnswer(inv -> {
            final HourUsage u = inv.getArgument(0, HourUsage.class);
            return HourUsage.builder()
                    .id(7L)
                    .academy(u.getAcademy())
                    .pilot(u.getPilot())
                    .aircraft(u.getAircraft())
                    .instructor(u.getInstructor())
                    .hours(u.getHours())
                    .flightDate(u.getFlightDate())
                    .logbookNumber(u.getLogbookNumber())
                    .createdBy(u.getCreatedBy())
                    .build();
        });
        when(balanceRepository.save(any(UserAircraftBalance.class))).thenAnswer(inv -> inv.getArgument(0, UserAircraftBalance.class));

        final RegisterUsageResponseDto dto = useCase.apply(req);

        assertNotNull(dto);
        assertEquals(7L, dto.getUsageId());
        assertEquals(2.0, dto.getBalanceHours());

        verify(academyRepository).findById(1L);
        verify(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        verify(userRepository).findById(10L);
        verify(userRepository).findById(11L);
        verify(aircraftRepository).findById(100L);
        verify(balanceRepository).findByPilotAndAircraft(pilot, aircraft);
        verify(userRepository).findByUsername("admin");
        verify(userActivePolicy).apply(creator);
        verify(hourUsageRepository).save(any(HourUsage.class));
        verify(balanceRepository).save(any(UserAircraftBalance.class));
    }

    @Test
    void insufficient_balance_throws_business() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(10L).username("p1").academy(academy).fullName("P").password("x").build();
        final User instructor = User.builder().id(11L).username("i1").academy(academy).fullName("I").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).tailNumber("HK-1").model("M").type("SEL").build();
        final UserAircraftBalance balance = UserAircraftBalance.builder()
                .pilot(pilot).aircraft(aircraft).totalPurchased(1).totalUsed(0).balanceHours(1).build();
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(10L);
        req.setInstructorId(11L);
        req.setAircraftId(100L);
        req.setHours(2.0);
        req.setFlightDate(LocalDate.now());
        req.setLogbookNumber("L-1");

        when(academyRepository.findById(1L)).thenReturn(Optional.of(academy));
        doNothing().when(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        when(userRepository.findById(10L)).thenReturn(Optional.of(pilot));
        when(userRepository.findById(11L)).thenReturn(Optional.of(instructor));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(balanceRepository.findByPilotAndAircraft(pilot, aircraft)).thenReturn(Optional.of(balance));

        assertThrows(BusinessException.class, () -> useCase.apply(req));

        verify(academyRepository).findById(1L);
        verify(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        verify(userRepository).findById(10L);
        verify(userRepository).findById(11L);
        verify(aircraftRepository).findById(100L);
        verify(balanceRepository).findByPilotAndAircraft(pilot, aircraft);
    }

    @Test
    void missing_entities_throw_not_found() {
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        when(academyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(req));
        verify(academyRepository).findById(1L);
    }
}
