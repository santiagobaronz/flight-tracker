package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.core.policy.AccessValidationUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.UserAircraftBalance;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import com.espectrosoft.flightTracker.domain.repository.UserAircraftBalanceRepository;
import com.espectrosoft.flightTracker.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBalanceUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private UserAircraftBalanceRepository balanceRepository;
    @Mock
    private AccessValidationUseCase accessValidationUseCase;

    private GetBalanceUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBalanceUseCaseImpl(
                userRepository,
                aircraftRepository,
                balanceRepository,
                accessValidationUseCase
        );
    }

    @Test
    void returns_existing_balance() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(10L).username("p1").academy(academy).fullName("P").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).tailNumber("HK-1").model("M").type("SEL").build();
        final UserAircraftBalance balance = UserAircraftBalance.builder()
                .pilot(pilot).aircraft(aircraft).totalPurchased(5).totalUsed(2).balanceHours(3).build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(pilot));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        doNothing().when(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        when(balanceRepository.findByPilotAndAircraft(pilot, aircraft)).thenReturn(Optional.of(balance));

        final UserAircraftBalanceDto dto = useCase.apply(10L, 100L);

        assertNotNull(dto);
        assertEquals(10L, dto.getPilotId());
        assertEquals(100L, dto.getAircraftId());
        assertEquals(5.0, dto.getTotalPurchased());
        assertEquals(2.0, dto.getTotalUsed());
        assertEquals(3.0, dto.getBalanceHours());

        verify(userRepository).findById(10L);
        verify(aircraftRepository).findById(100L);
        verify(accessValidationUseCase).apply(academy, ModuleCode.HOURS);
        verify(balanceRepository).findByPilotAndAircraft(pilot, aircraft);
    }

    @Test
    void missing_user_throws_not_found() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(10L, 100L));
        verify(userRepository).findById(10L);
    }
}
