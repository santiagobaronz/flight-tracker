package com.espectrosoft.flightTracker.application.dto.module;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleToggleRequestDto {
    @NotNull
    private Long academyId;
    @NotNull
    private ModuleCode moduleCode;
    @NotNull
    private Boolean active;
}
