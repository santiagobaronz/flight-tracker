package com.espectrosoft.flightTracker.application.core.policy.validations;

import com.espectrosoft.flightTracker.domain.model.User;

public interface UserActivePolicy {
    void apply(User user);
}
