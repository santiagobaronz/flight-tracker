package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.ListRolesUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListRolesUseCaseImpl implements ListRolesUseCase {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDto> apply() {
        return roleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private RoleDto toDto(Role role) {
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
