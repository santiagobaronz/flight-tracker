package com.espectrosoft.flightTracker.application.usecase;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;

public interface ModuleUseCase {
    ModuleStatusDto toggle(ModuleToggleRequestDto request);
    ModuleStatusDto status(Long academyId, ModuleCode moduleCode);
}
