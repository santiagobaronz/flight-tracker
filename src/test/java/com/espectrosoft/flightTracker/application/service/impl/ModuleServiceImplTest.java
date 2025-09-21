package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.ToggleModuleUseCase;
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

    private ModuleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ModuleServiceImpl(toggleModuleUseCase, getModuleStatusUseCase);
    }

    @Test
    void toggle_delegates_to_usecase() {
        final ModuleToggleRequestDto req = new ModuleToggleRequestDto();
        req.setAcademyId(1L);
        req.setModuleCode(ModuleCode.HOURS);
        req.setActive(Boolean.TRUE);

        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, true);
        when(toggleModuleUseCase.apply(eq(req))).thenReturn(expected);

        final ModuleStatusDto resp = service.toggle(req);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(true, resp.isActive());
        verify(toggleModuleUseCase).apply(eq(req));
    }

    @Test
    void status_delegates_to_usecase() {
        final ModuleStatusDto expected = new ModuleStatusDto(1L, ModuleCode.HOURS, false);
        when(getModuleStatusUseCase.apply(eq(1L), eq(ModuleCode.HOURS))).thenReturn(expected);

        final ModuleStatusDto resp = service.status(1L, ModuleCode.HOURS);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(false, resp.isActive());
        verify(getModuleStatusUseCase).apply(eq(1L), eq(ModuleCode.HOURS));
    }
}
