package com.espectrosoft.flightTracker.application.core.policy.validations.impl;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.exception.types.AcademyInactiveException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AcademyActivePolicyImpl implements AcademyActivePolicy {

    @Override
    public void apply(Academy academy) {
        if (!nonNull(academy) || !academy.isActive()) {
            throw new AcademyInactiveException("Academy is inactive");
        }
    }
}
