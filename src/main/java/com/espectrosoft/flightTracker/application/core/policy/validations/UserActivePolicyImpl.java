package com.espectrosoft.flightTracker.application.core.policy.validations;

import com.espectrosoft.flightTracker.application.exception.UserInactiveException;
import com.espectrosoft.flightTracker.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserActivePolicyImpl implements UserActivePolicy {
    @Override
    public void apply(User user) {
        if (!Objects.nonNull(user)) {
            throw new UserInactiveException("User is null");
        }
        if (!user.isActive()) {
            throw new UserInactiveException("User is inactive");
        }
    }
}
