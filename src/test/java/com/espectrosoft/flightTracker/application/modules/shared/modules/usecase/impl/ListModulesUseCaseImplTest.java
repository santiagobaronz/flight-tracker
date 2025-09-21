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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListModulesUseCaseImplTest {

    @Mock
    private AcademyModuleRepository academyModuleRepository;
    @Mock
    private DomainLookup domainLookup;

    private ListModulesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListModulesUseCaseImpl(academyModuleRepository, domainLookup);
    }

    @Test
    void list_modules_maps_all_fields() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final AcademyModule m1 = AcademyModule.builder()
                .id(10L)
                .academy(academy)
                .section(ModuleSection.APPLICATION)
                .moduleCode(ModuleCode.HOURS)
                .active(true)
                .name("Horas")
                .description("Gestión de horas")
                .route("/app/hours")
                .build();
        final AcademyModule m2 = AcademyModule.builder()
                .id(11L)
                .academy(academy)
                .section(ModuleSection.MANAGEMENT)
                .moduleCode(ModuleCode.HOURS)
                .active(false)
                .name("Horas Mgmt")
                .description("Admin de horas")
                .route("/mgmt/hours")
                .build();

        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(academyModuleRepository.findByAcademy(eq(academy))).thenReturn(List.of(m1, m2));

        final List<ModuleInfoDto> res = useCase.apply(1L);

        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals(ModuleSection.APPLICATION, res.get(0).getSection());
        assertEquals("Horas", res.get(0).getName());
        assertEquals("/mgmt/hours", res.get(1).getRoute());
        verify(domainLookup).requireAcademy(eq(1L));
        verify(academyModuleRepository).findByAcademy(eq(academy));
    }
}
