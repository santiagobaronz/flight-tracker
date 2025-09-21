package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.access.PublicAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AircraftServiceImplTest {

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private DomainLookup domainLookup;
    @Mock
    private PublicAccessPolicy publicAccessPolicy;
    @Mock
    private InternalAccessPolicy internalAccessPolicy;

    private AircraftServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AircraftServiceImpl(aircraftRepository, domainLookup, publicAccessPolicy, internalAccessPolicy);
    }

    @Test
    void list_by_academy_returns_dtos() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        final Aircraft a1 = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M1").type(AircraftType.AIRCRAFT).build();
        final Aircraft a2 = Aircraft.builder().id(200L).academy(academy).registration("HK-2").model("M2").type(AircraftType.SIMULATOR).build();

        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.findByAcademy(eq(academy))).thenReturn(List.of(a1, a2));

        final List<AircraftDto> res = service.listByAcademy(1L);

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(domainLookup).requireCurrentUser();
        verify(publicAccessPolicy).validate(eq(academy));
        verify(aircraftRepository).findByAcademy(eq(academy));
    }

    @Test
    void update_changes_fields_and_saves() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("admin").academy(academy).fullName("Admin").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();

        final UpdateAircraftRequestDto req = new UpdateAircraftRequestDto();
        req.setRegistration("HK-1");
        req.setModel("M-NEW");
        req.setType(AircraftType.SIMULATOR);

        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        final AircraftDto dto = service.update(100L, req);

        assertNotNull(dto);
        assertEquals("M-NEW", dto.getModel());
        assertEquals(AircraftType.SIMULATOR, dto.getType());
        verify(domainLookup).requireAircraft(eq(100L));
        verify(domainLookup).requireCurrentUser();
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(aircraftRepository).save(any(Aircraft.class));
    }

    @Test
    void update_duplicate_registration_throws_business() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("admin").academy(academy).fullName("Admin").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();
        final Aircraft other = Aircraft.builder().id(200L).academy(academy).registration("HK-2").model("M2").type(AircraftType.AIRCRAFT).build();

        final UpdateAircraftRequestDto req = new UpdateAircraftRequestDto();
        req.setRegistration("HK-2");
        req.setModel("M");
        req.setType(AircraftType.AIRCRAFT);

        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(aircraftRepository.findByAcademyAndRegistration(eq(academy), eq("HK-2"))).thenReturn(Optional.of(other));

        assertThrows(BusinessException.class, () -> service.update(100L, req));
        verify(domainLookup).requireAircraft(eq(100L));
        verify(domainLookup).requireCurrentUser();
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(aircraftRepository).findByAcademyAndRegistration(eq(academy), eq("HK-2"));
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

        final AircraftDto dto = service.setActive(100L, req);

        assertNotNull(dto);
        assertFalse(dto.isActive());
        verify(domainLookup).requireAircraft(eq(100L));
        verify(domainLookup).requireCurrentUser();
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(aircraftRepository).save(any(Aircraft.class));
    }
}
