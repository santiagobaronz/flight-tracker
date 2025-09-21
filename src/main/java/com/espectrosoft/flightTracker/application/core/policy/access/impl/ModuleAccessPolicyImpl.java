package com.espectrosoft.flightTracker.application.core.policy.access.impl;

import static com.espectrosoft.flightTracker.application.core.policy.constants.PolicyConstants.INSUFFICIENT_PERMISSIONS;

import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ModuleAccessPolicyImpl implements ModuleAccessPolicy {

    AcademyActivePolicy academyActivePolicy;
    UserActivePolicy userActivePolicy;
    ModuleEnabledPolicy moduleEnabledPolicy;
    RolePermissionRepository rolePermissionRepository;

    @Override
    public void validate(Academy academy, User user, ModuleSection section, ModuleCode moduleCode, PermissionAction action) {
        academyActivePolicy.apply(academy);
        userActivePolicy.apply(user);
        moduleEnabledPolicy.apply(academy, section, moduleCode);
        final boolean hasPermission = rolePermissionRepository.existsByUserIdAndModuleCodeAndAction(user.getId(), moduleCode, action);
        if (!hasPermission) {
            throw new BusinessException(INSUFFICIENT_PERMISSIONS);
        }
    }
}
