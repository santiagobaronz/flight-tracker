package com.espectrosoft.flightTracker.application.modules.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleModuleUseCaseImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private AcademyModuleRepository academyModuleRepository;
    @Mock
    private AcademyActivePolicy academyActivePolicy;

    @InjectMocks
    private ToggleModuleUseCaseImpl useCase;

    @Test
    void toggle_creates_or_updates() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final ModuleToggleRequestDto req = new ModuleToggleRequestDto();
        req.setAcademyId(1L);
        req.setModuleCode(ModuleCode.HOURS);
        req.setActive(Boolean.TRUE);

        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.empty());
        when(academyModuleRepository.save(any(AcademyModule.class))).thenAnswer(inv -> {
            final AcademyModule am = inv.getArgument(0, AcademyModule.class);
            return AcademyModule.builder().id(10L).academy(am.getAcademy()).moduleCode(am.getModuleCode()).active(am.isActive()).build();
        });

        final ModuleStatusDto resp = useCase.apply(req);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(true, resp.isActive());
        verify(academyRepository).findById(eq(1L));
        verify(academyModuleRepository).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(academyModuleRepository).save(any(AcademyModule.class));
    }
}
