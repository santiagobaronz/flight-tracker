package com.espectrosoft.flightTracker.application.dto.module;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModuleStatusDto {
    private Long academyId;
    private ModuleCode moduleCode;
    private boolean active;
}
