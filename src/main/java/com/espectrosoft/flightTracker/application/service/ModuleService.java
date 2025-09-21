package com.espectrosoft.flightTracker.application.service;

import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;

import java.util.List;

public interface ModuleService {
    ModuleStatusDto toggle(ModuleToggleRequestDto request);
    ModuleStatusDto status(Long academyId, ModuleCode moduleCode);
    List<ModuleInfoDto> listAll(Long academyId);
    ModuleInfoDto info(Long academyId, ModuleSection section, ModuleCode moduleCode);
}
