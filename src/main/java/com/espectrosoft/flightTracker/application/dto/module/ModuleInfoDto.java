package com.espectrosoft.flightTracker.application.dto.module;

import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleInfoDto {
    private Long academyId;
    private ModuleSection section;
    private ModuleCode moduleCode;
    private boolean active;
    private String name;
    private String description;
    private String route;
    private List<String> attributes;
}
