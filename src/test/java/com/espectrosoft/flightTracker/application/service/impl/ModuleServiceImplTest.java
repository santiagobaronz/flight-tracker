package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.modules.management.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.application.modules.management.modules.usecase.ToggleModuleUseCase;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

    @Mock
    private ToggleModuleUseCase toggleModuleUseCase;
    @Mock
    private GetModuleStatusUseCase getModuleStatusUseCase;
    @Mock
    private InternalAccessPolicy internalAccessPolicy;
    @Mock
    private DomainLookup domainLookup;

    private ModuleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ModuleServiceImpl(toggleModuleUseCase, getModuleStatusUseCase, internalAccessPolicy, domainLookup);
    }

    @Test
    void toggle_delegates_to_usecase() {
        final ModuleToggleRequestDto req = new ModuleToggleRequestDto();
        req.setAcademyId(1L);
        req.setModuleCode(ModuleCode.HOURS);
        req.setActive(Boolean.TRUE);
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, true);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(toggleModuleUseCase.apply(eq(req))).thenReturn(expected);

        final ModuleStatusDto resp = service.toggle(req);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(true, resp.isActive());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(toggleModuleUseCase).apply(eq(req));
    }

    @Test
    void status_delegates_to_usecase() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, false);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(getModuleStatusUseCase.apply(eq(1L), eq(ModuleCode.HOURS))).thenReturn(expected);

        final ModuleStatusDto resp = service.status(1L, ModuleCode.HOURS);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(false, resp.isActive());
        verify(internalAccessPolicy).validate(eq(academy), eq(current));
        verify(getModuleStatusUseCase).apply(eq(1L), eq(ModuleCode.HOURS));
    }
}
