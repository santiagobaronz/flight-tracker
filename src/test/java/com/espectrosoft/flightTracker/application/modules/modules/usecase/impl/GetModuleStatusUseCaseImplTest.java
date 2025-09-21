package com.espectrosoft.flightTracker.application.modules.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.domain.model.Academy;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetModuleStatusUseCaseImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private AcademyModuleRepository academyModuleRepository;

    @InjectMocks
    private GetModuleStatusUseCaseImpl useCase;

    @Test
    void status_defaults_false_when_absent() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.empty());

        final ModuleStatusDto resp = useCase.apply(1L, ModuleCode.HOURS);

        assertEquals(1L, resp.getAcademyId());
        assertEquals(ModuleCode.HOURS, resp.getModuleCode());
        assertEquals(false, resp.isActive());
        verify(academyRepository).findById(eq(1L));
        verify(academyModuleRepository).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
    }
}
