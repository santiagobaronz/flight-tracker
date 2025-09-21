package com.espectrosoft.flightTracker.application.core.policy.validations.impl;

import static com.espectrosoft.flightTracker.application.core.policy.constants.PolicyConstants.USER_INACTIVE;
import static java.util.Objects.nonNull;

import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.application.exception.types.UserInactiveException;
import com.espectrosoft.flightTracker.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserActivePolicyImpl implements UserActivePolicy {
    @Override
    public void apply(User user) {
        if (!nonNull(user) || !user.isActive()) {
            throw new UserInactiveException(USER_INACTIVE);
        }
    }
}
