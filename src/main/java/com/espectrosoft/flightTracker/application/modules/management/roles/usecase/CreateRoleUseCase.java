package com.espectrosoft.flightTracker.application.modules.management.roles.usecase;

import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.domain.model.User;

public interface CreateRoleUseCase {
    RoleDto apply(CreateRoleRequestDto request, User actor);
}
