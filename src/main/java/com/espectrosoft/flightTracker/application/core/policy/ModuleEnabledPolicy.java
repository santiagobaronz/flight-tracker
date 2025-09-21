package com.espectrosoft.flightTracker.application.core.policy;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
public interface ModuleEnabledPolicy {
    void apply(Academy academy, ModuleCode module);
}
