package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;

import java.util.List;

public interface GetRolePermissionsUseCase {
    List<RolePermissionDto> apply(Long roleId);
}
