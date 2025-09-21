package com.espectrosoft.flightTracker.application.core.policy.access;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;

public interface InternalAccessPolicy {

    void validate(Academy academy, User user);
    void validationForLogin(User user);
}
