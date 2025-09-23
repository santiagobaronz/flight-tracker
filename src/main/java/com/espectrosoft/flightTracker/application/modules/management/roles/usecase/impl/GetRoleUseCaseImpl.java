package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.GetRoleUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoleUseCaseImpl implements GetRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto apply(Long id) {
        final Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
        final Long createdById = role.getCreatedBy() != null ? role.getCreatedBy().getId() : null;
        final Long updatedById = role.getUpdatedBy() != null ? role.getUpdatedBy().getId() : null;
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .roleType(role.getRoleType())
                .createdById(createdById)
                .updatedById(updatedById)
                .build();
    }
}
