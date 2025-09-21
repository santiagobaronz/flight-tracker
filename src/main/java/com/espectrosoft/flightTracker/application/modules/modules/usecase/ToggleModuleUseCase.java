package com.espectrosoft.flightTracker.application.modules.modules.usecase;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;

public interface ToggleModuleUseCase {
    ModuleStatusDto apply(ModuleToggleRequestDto request);
}
