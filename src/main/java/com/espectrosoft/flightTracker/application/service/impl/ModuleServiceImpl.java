package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.ToggleModuleUseCase;
import com.espectrosoft.flightTracker.application.service.ModuleService;
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

    @Override
    public ModuleStatusDto toggle(ModuleToggleRequestDto request) {
        return toggleModuleUseCase.apply(request);
    }

    @Override
    public ModuleStatusDto status(Long academyId, ModuleCode moduleCode) {
        return getModuleStatusUseCase.apply(academyId, moduleCode);
    }
}
