package com.espectrosoft.flightTracker.application.core.policy.validations.impl;

import com.espectrosoft.flightTracker.application.exception.types.ModuleDisabledException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleEnabledPolicyImplTest {

    @Mock
    private AcademyModuleRepository academyModuleRepository;

    private ModuleEnabledPolicyImpl
        policy;

    @BeforeEach
    void setUp() {
        policy = new ModuleEnabledPolicyImpl(academyModuleRepository);
    }

    @Test
    void apply_active_module_ok() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final AcademyModule am = AcademyModule.builder().academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(am));

        assertDoesNotThrow(() -> policy.apply(academy, ModuleSection.APPLICATION, ModuleCode.HOURS));
        verify(academyModuleRepository).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
    }

    @Test
    void apply_absent_module_throws() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.empty());

        assertThrows(ModuleDisabledException.class, () -> policy.apply(academy, ModuleSection.APPLICATION, ModuleCode.HOURS));
        verify(academyModuleRepository).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
    }

    @Test
    void apply_inactive_module_throws() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final AcademyModule am = AcademyModule.builder().academy(academy).moduleCode(ModuleCode.HOURS).active(false).build();
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(am));

        assertThrows(ModuleDisabledException.class, () -> policy.apply(academy, ModuleSection.APPLICATION, ModuleCode.HOURS));
        verify(academyModuleRepository).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
    }
}
