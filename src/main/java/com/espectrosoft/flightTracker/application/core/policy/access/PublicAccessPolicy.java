package com.espectrosoft.flightTracker.application.core.policy.access;

import com.espectrosoft.flightTracker.domain.model.Academy;

public interface PublicAccessPolicy {
    void validate(Academy academy);
}
