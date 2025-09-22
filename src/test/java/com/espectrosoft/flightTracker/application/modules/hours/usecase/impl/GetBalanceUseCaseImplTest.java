package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.impl.GetBalanceUseCaseImpl;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.UserAircraftBalance;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
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

    private GetBalanceUseCaseImpl
        useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetBalanceUseCaseImpl(
                userRepository,
                aircraftRepository,
                balanceRepository
        );
    }

    @Test
    void returns_existing_balance() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User client = User.builder().id(10L).username("c1").academy(academy).fullName("C").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();
        final UserAircraftBalance balance = UserAircraftBalance.builder()
                .client(client).aircraft(aircraft).totalPurchased(5).totalUsed(2).balanceHours(3).build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(client));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(balanceRepository.findByClientAndAircraft(client, aircraft)).thenReturn(Optional.of(balance));

        final UserAircraftBalanceDto dto = useCase.apply(10L, 100L);

        assertNotNull(dto);
        assertEquals(10L, dto.getClientId());
        assertEquals(100L, dto.getAircraftId());
        assertEquals(5.0, dto.getTotalPurchased());
        assertEquals(2.0, dto.getTotalUsed());
        assertEquals(3.0, dto.getBalanceHours());

        verify(userRepository).findById(10L);
        verify(aircraftRepository).findById(100L);
        verify(balanceRepository).findByClientAndAircraft(client, aircraft);
    }

    @Test
    void missing_user_throws_not_found() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(10L, 100L));
        verify(userRepository).findById(10L);
    }
}
