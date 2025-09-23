package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.DeleteRoleUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.enums.RoleType;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteRoleUseCaseImpl implements DeleteRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public void apply(Long id) {
        final Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
        if (role.getRoleType() == RoleType.MANDATORY) {
            throw new BusinessException("Mandatory role cannot be deleted");
        }
        final long assignments = roleRepository.countUsersByRoleId(role.getId());
        if (assignments > 0) {
            throw new ConflictException("Role has assigned users");
        }
        roleRepository.delete(role);
    }
}
