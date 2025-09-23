package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;
import com.espectrosoft.flightTracker.domain.model.User;

public interface UpdateRoleUseCase {
    RoleDto apply(Long id, UpdateRoleRequestDto request, User actor);
}
