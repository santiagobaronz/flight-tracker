package com.espectrosoft.flightTracker.application.dto.module;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ModuleStatusDto {
    private Long academyId;
    private ModuleCode moduleCode;
    private boolean active;
    private List<String> attributes;

    public ModuleStatusDto(Long academyId, ModuleCode moduleCode, boolean active) {
        this.academyId = academyId;
        this.moduleCode = moduleCode;
        this.active = active;
    }
}
