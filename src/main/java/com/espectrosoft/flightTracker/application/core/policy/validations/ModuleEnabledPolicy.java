package com.espectrosoft.flightTracker.application.core.policy.validations;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
public interface ModuleEnabledPolicy {
    void apply(Academy academy, ModuleSection section, ModuleCode module);
}
