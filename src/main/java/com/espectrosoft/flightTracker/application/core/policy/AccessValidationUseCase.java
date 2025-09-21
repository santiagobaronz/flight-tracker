package com.espectrosoft.flightTracker.application.core.policy;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;

public interface AccessValidationUseCase {
    void apply(Academy academy, ModuleCode moduleCode);
}
