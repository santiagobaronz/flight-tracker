package com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.PublicAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAircraftUseCaseImplTest {

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private DomainLookup domainLookup;
    @Mock
    private PublicAccessPolicy publicAccessPolicy;

    private ListAircraftUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListAircraftUseCaseImpl(aircraftRepository, domainLookup, publicAccessPolicy);
    }

    @Test
    void list_only_active_returns_active() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        final Aircraft a1 = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M1").type(AircraftType.AIRCRAFT).build();
        final Aircraft a2 = Aircraft.builder().id(200L).academy(academy).registration("HK-2").model("M2").type(AircraftType.SIMULATOR).build();

        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.findByAcademyAndIsActiveTrue(eq(academy))).thenReturn(List.of(a1, a2));

        final List<AircraftDto> res = useCase.apply(1L, true);

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(domainLookup).requireCurrentUser();
        verify(publicAccessPolicy).validate(eq(academy));
        verify(aircraftRepository).findByAcademyAndIsActiveTrue(eq(academy));
    }

    @Test
    void list_all_returns_all() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        final Aircraft a1 = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M1").type(AircraftType.AIRCRAFT).build();
        final Aircraft a2 = Aircraft.builder().id(200L).academy(academy).registration("HK-2").model("M2").type(AircraftType.SIMULATOR).build();

        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.findByAcademy(eq(academy))).thenReturn(List.of(a1, a2));

        final List<AircraftDto> res = useCase.apply(1L, false);

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(domainLookup).requireCurrentUser();
        verify(publicAccessPolicy).validate(eq(academy));
        verify(aircraftRepository).findByAcademy(eq(academy));
    }
}
