package com.espectrosoft.flightTracker.application.core.policy.access.impl;

import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ModuleAccessPolicyImpl implements ModuleAccessPolicy {

    AcademyActivePolicy
        academyActivePolicy;
    UserActivePolicy
        userActivePolicy;
    ModuleEnabledPolicy
        moduleEnabledPolicy;

    @Override
    public void validate(Academy academy, User user, ModuleCode moduleCode) {
        academyActivePolicy.apply(academy);
        userActivePolicy.apply(user);
        moduleEnabledPolicy.apply(academy, moduleCode);
    }
}
