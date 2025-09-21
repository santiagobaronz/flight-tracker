package com.espectrosoft.flightTracker.application.modules.management.modules.usecase;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;

public interface GetModuleStatusUseCase {
    ModuleStatusDto apply(Long academyId, ModuleCode moduleCode);
}
