package com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetAircraftActiveUseCaseImplTest {

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private DomainLookup domainLookup;
    @Mock
    private InternalAccessPolicy internalAccessPolicy;

    private SetAircraftActiveUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new SetAircraftActiveUseCaseImpl(aircraftRepository, domainLookup, internalAccessPolicy);
    }

    @Test
    void set_active_updates_flag_and_saves() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("admin").academy(academy).fullName("Admin").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();

        final SetActiveRequestDto req = new SetActiveRequestDto();
        req.setActive(false);

        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        final AircraftDto dto = useCase.apply(100L, req);

        assertNotNull(dto);
        assertFalse(dto.isActive());
        verify(domainLookup).requireAircraft(eq(100L));
        verify(domainLookup).requireCurrentUser();
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(aircraftRepository).save(any(Aircraft.class));
    }

    @Test
    void set_active_with_user_in_other_academy_throws_business() {
        final Academy academyA = Academy.builder().id(1L).name("A").build();
        final Academy academyB = Academy.builder().id(2L).name("B").build();
        final User current = User.builder().id(10L).username("admin").academy(academyB).fullName("Admin").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academyA).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();

        final SetActiveRequestDto req = new SetActiveRequestDto();
        req.setActive(true);

        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);

        assertThrows(BusinessException.class, () -> useCase.apply(100L, req));
        verify(domainLookup).requireAircraft(eq(100L));
        verify(domainLookup).requireCurrentUser();
        verify(internalAccessPolicy, never()).validate(eq(academyA), eq(current));
        verify(aircraftRepository, never()).save(any(Aircraft.class));
    }
}
