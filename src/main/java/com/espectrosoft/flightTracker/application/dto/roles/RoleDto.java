package com.espectrosoft.flightTracker.application.dto.roles;

import com.espectrosoft.flightTracker.domain.model.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private RoleType roleType;
    private Long createdById;
    private Long updatedById;
}
