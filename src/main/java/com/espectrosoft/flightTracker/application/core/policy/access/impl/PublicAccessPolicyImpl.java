package com.espectrosoft.flightTracker.application.core.policy.access.impl;

import com.espectrosoft.flightTracker.application.core.policy.access.PublicAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublicAccessPolicyImpl implements PublicAccessPolicy {

    AcademyActivePolicy
        academyActivePolicy;

    @Override
    public void validate(Academy academy) {
        academyActivePolicy.apply(academy);
    }
}
