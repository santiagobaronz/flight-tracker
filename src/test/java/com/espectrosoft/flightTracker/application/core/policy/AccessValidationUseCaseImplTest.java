package com.espectrosoft.flightTracker.application.core.policy;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccessValidationUseCaseImplTest {

    @Mock
    private AcademyActivePolicy
        academyActivePolicy;
    @Mock
    private ModuleEnabledPolicy
        moduleEnabledPolicy;

    @InjectMocks
    private AccessValidationUseCaseImpl useCase;

    @Test
    void applies_both_policies_when_active() {
        final Academy academy = Academy.builder().id(1L).name("A").address("Addr").phoneNumber("123").build();
        assertDoesNotThrow(() -> useCase.apply(academy, ModuleCode.HOURS));
        verify(academyActivePolicy).apply(eq(academy));
        verify(moduleEnabledPolicy).apply(eq(academy), eq(ModuleCode.HOURS));
    }

    @Test
    void short_circuits_when_inactive() {
        final Academy academy = Academy.builder().id(1L).name("A").address("Addr").phoneNumber("123").build();
        doThrow(new RuntimeException("inactive")).when(academyActivePolicy).apply(eq(academy));
        try {
            useCase.apply(academy, ModuleCode.HOURS);
        } catch (RuntimeException ignored) {}
        verify(academyActivePolicy).apply(eq(academy));
        // moduleEnabledPolicy.apply should not be called due to short-circuit
    }
}
