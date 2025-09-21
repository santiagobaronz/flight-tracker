package com.espectrosoft.flightTracker.application.modules.shared.modules.usecase;

import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;

import java.util.List;

public interface ListModulesUseCase {
    List<ModuleInfoDto> apply(Long academyId);
}
