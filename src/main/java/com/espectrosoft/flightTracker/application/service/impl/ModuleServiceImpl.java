package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.ToggleModuleUseCase;
import com.espectrosoft.flightTracker.application.service.ModuleService;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ModuleServiceImpl implements ModuleService {

    ToggleModuleUseCase toggleModuleUseCase;
    GetModuleStatusUseCase getModuleStatusUseCase;
    InternalAccessPolicy internalAccessPolicy;
    DomainLookup domainLookup;

    @Override
    public ModuleStatusDto toggle(ModuleToggleRequestDto request) {
        final Academy academy = domainLookup.requireAcademy(request.getAcademyId());
        final User currentUser = domainLookup.requireCurrentUser();
        internalAccessPolicy.validate(academy, currentUser);
        return toggleModuleUseCase.apply(request);
    }

    @Override
    public ModuleStatusDto status(Long academyId, ModuleCode moduleCode) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final User currentUser = domainLookup.requireCurrentUser();
        internalAccessPolicy.validate(academy, currentUser);
        return getModuleStatusUseCase.apply(academyId, moduleCode);
    }
}
