package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;

import java.util.List;

public interface ListRolesUseCase {
    List<RoleDto> apply();
}
