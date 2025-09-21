package com.espectrosoft.flightTracker.application.core.policy.access.impl;

import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InternalAccessPolicyImpl implements InternalAccessPolicy {

    AcademyActivePolicy
        academyActivePolicy;
    UserActivePolicy
        userActivePolicy;

    @Override
    public void validate(Academy academy, User user) {
        academyActivePolicy.apply(academy);
        userActivePolicy.apply(user);
    }

    @Override
    public void validationForLogin(User user) {
        userActivePolicy.apply(user);
    }
}
