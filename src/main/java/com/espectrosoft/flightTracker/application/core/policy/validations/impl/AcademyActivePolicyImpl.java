package com.espectrosoft.flightTracker.application.core.policy.validations.impl;

import static com.espectrosoft.flightTracker.application.core.policy.constants.PolicyConstants.ACADEMY_INACTIVE;
import static java.util.Objects.nonNull;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.exception.types.AcademyInactiveException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AcademyActivePolicyImpl implements AcademyActivePolicy {

    @Override
    public void apply(Academy academy) {
        if (!nonNull(academy) || !academy.isActive()) {
            throw new AcademyInactiveException(ACADEMY_INACTIVE);
        }
    }
}
