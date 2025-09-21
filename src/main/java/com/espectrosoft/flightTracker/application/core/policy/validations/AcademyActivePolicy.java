package com.espectrosoft.flightTracker.application.core.policy.validations;

import com.espectrosoft.flightTracker.domain.model.Academy;

public interface AcademyActivePolicy {
    void apply(Academy academy);
}
