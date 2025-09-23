package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;

public interface GetRoleUseCase {
    RoleDto apply(Long id);
}
