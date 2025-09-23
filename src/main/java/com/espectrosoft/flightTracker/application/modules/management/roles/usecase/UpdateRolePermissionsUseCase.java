package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;

import java.util.List;

public interface UpdateRolePermissionsUseCase {
    List<RolePermissionDto> apply(Long roleId, UpdateRolePermissionsRequestDto request);
}
