package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.SetAircraftActiveUseCase;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.UpdateAircraftUseCase;
import com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase.ListAircraftUseCase;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AircraftServiceImplTest {

    @Mock
    private ListAircraftUseCase listAircraftUseCase;
    @Mock
    private UpdateAircraftUseCase updateAircraftUseCase;
    @Mock
    private SetAircraftActiveUseCase setAircraftActiveUseCase;
    @Mock
    private ModuleAccessPolicy moduleAccessPolicy;
    @Mock
    private DomainLookup domainLookup;

    private AircraftServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AircraftServiceImpl(listAircraftUseCase, updateAircraftUseCase, setAircraftActiveUseCase, moduleAccessPolicy, domainLookup);
    }

    @Test
    void list_by_academy_returns_dtos() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(listAircraftUseCase.apply(eq(1L), eq(true))).thenReturn(List.of(
                AircraftDto.builder().id(1L).registration("HK-1").build(),
                AircraftDto.builder().id(2L).registration("HK-2").build()
        ));

        final List<AircraftDto> res = service.listByAcademy(1L);

        assertNotNull(res);
        assertEquals(2, res.size());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(domainLookup).requireCurrentUser();
        verify(moduleAccessPolicy).validate(eq(academy), eq(current), eq(ModuleSection.APPLICATION), eq(ModuleCode.AIRCRAFT), eq(PermissionAction.VIEW));
        verify(listAircraftUseCase).apply(eq(1L), eq(true));
    }

    @Test
    void update_changes_fields_and_saves() {
        final UpdateAircraftRequestDto req = new UpdateAircraftRequestDto();
        req.setRegistration("HK-1");
        req.setModel("M-NEW");
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").build();
        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(updateAircraftUseCase.apply(eq(100L), eq(req))).thenReturn(AircraftDto.builder().id(100L).model("M-NEW").build());

        final AircraftDto dto = service.update(100L, req);

        assertNotNull(dto);
        assertEquals("M-NEW", dto.getModel());
        verify(moduleAccessPolicy).validate(eq(academy), eq(current), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.AIRCRAFT), eq(PermissionAction.EDIT));
        verify(updateAircraftUseCase).apply(eq(100L), eq(req));
    }

    @Test
    void set_active_updates_flag_and_saves() {
        final SetActiveRequestDto req = new SetActiveRequestDto();
        req.setActive(false);
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(10L).username("u").academy(academy).fullName("U").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").build();
        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(setAircraftActiveUseCase.apply(eq(100L), eq(req))).thenReturn(AircraftDto.builder().id(100L).active(false).build());

        final AircraftDto dto = service.setActive(100L, req);

        assertNotNull(dto);
        assertFalse(dto.isActive());
        verify(moduleAccessPolicy).validate(eq(academy), eq(current), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.AIRCRAFT), eq(PermissionAction.EDIT));
        verify(setAircraftActiveUseCase).apply(eq(100L), eq(req));
    }
}
