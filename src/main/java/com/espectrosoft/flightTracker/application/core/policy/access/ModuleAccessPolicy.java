package com.espectrosoft.flightTracker.application.core.policy.access;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;

public interface ModuleAccessPolicy {
    void validate(Academy academy, User user, ModuleCode moduleCode);
}
