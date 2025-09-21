package com.espectrosoft.flightTracker.application.core.policy.validations;

import com.espectrosoft.flightTracker.application.exception.ModuleDisabledException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
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
    public void apply(Academy academy, ModuleCode module) {
        academyModuleRepository.findByAcademyAndModuleCode(academy, module)
                .filter(AcademyModule::isActive)
                .orElseThrow(() -> new ModuleDisabledException("Module is disabled for this academy"));
    }
}
