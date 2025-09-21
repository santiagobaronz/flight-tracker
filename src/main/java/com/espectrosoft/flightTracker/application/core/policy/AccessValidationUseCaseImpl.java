package com.espectrosoft.flightTracker.application.core.policy;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccessValidationUseCaseImpl implements AccessValidationUseCase {

    AcademyActivePolicy academyActivePolicy;
    ModuleEnabledPolicy moduleEnabledPolicy;

    @Override
    public void apply(Academy academy, ModuleCode moduleCode) {
        academyActivePolicy.apply(academy);
        moduleEnabledPolicy.apply(academy, moduleCode);
    }
}