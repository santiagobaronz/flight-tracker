package com.espectrosoft.flightTracker.application.core.policy.validations.impl;

import static com.espectrosoft.flightTracker.application.core.policy.constants.PolicyConstants.MODULE_DISABLED;

import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.application.exception.types.ModuleDisabledException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ModuleEnabledPolicyImpl implements ModuleEnabledPolicy {

    AcademyModuleRepository academyModuleRepository;

    @Override
    public void apply(Academy academy, ModuleSection section, ModuleCode module) {
        academyModuleRepository.findByAcademyAndSectionAndModuleCode(academy, section, module)
                .filter(AcademyModule::isActive)
                .orElseThrow(() -> new ModuleDisabledException(MODULE_DISABLED));
    }
}
