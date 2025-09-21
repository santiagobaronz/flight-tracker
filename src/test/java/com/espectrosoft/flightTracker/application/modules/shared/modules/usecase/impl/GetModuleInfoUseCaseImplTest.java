package com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetModuleInfoUseCaseImplTest {

    @Mock
    private AcademyModuleRepository academyModuleRepository;
    @Mock
    private DomainLookup domainLookup;

    private GetModuleInfoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetModuleInfoUseCaseImpl(academyModuleRepository, domainLookup);
    }

    @Test
    void info_returns_existing_module() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final AcademyModule am = AcademyModule.builder()
                .id(10L)
                .academy(academy)
                .section(ModuleSection.APPLICATION)
                .moduleCode(ModuleCode.HOURS)
                .active(true)
                .name("Horas")
                .description("Gestión de horas")
                .route("/app/hours")
                .build();

        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(academyModuleRepository.findByAcademyAndSectionAndModuleCode(eq(academy), eq(ModuleSection.APPLICATION), eq(ModuleCode.HOURS)))
                .thenReturn(Optional.of(am));

        final ModuleInfoDto dto = useCase.apply(1L, ModuleSection.APPLICATION, ModuleCode.HOURS);

        assertNotNull(dto);
        assertEquals(1L, dto.getAcademyId());
        assertEquals(ModuleSection.APPLICATION, dto.getSection());
        assertEquals(ModuleCode.HOURS, dto.getModuleCode());
        assertTrue(dto.isActive());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(academyModuleRepository).findByAcademyAndSectionAndModuleCode(eq(academy), eq(ModuleSection.APPLICATION), eq(ModuleCode.HOURS));
    }

    @Test
    void info_returns_inactive_when_absent() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(academyModuleRepository.findByAcademyAndSectionAndModuleCode(eq(academy), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.HOURS)))
                .thenReturn(Optional.empty());

        final ModuleInfoDto dto = useCase.apply(1L, ModuleSection.MANAGEMENT, ModuleCode.HOURS);

        assertNotNull(dto);
        assertEquals(1L, dto.getAcademyId());
        assertEquals(ModuleSection.MANAGEMENT, dto.getSection());
        assertEquals(ModuleCode.HOURS, dto.getModuleCode());
        assertFalse(dto.isActive());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(academyModuleRepository).findByAcademyAndSectionAndModuleCode(eq(academy), eq(ModuleSection.MANAGEMENT), eq(ModuleCode.HOURS));
    }
}
