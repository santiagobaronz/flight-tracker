package com.espectrosoft.flightTracker.application.modules.shared.modules.usecase;

import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;

public interface GetModuleInfoUseCase {
    ModuleInfoDto apply(Long academyId, ModuleSection section, ModuleCode moduleCode);
}
