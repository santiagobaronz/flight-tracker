package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.CreateRoleUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.RoleType;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class CreateRoleUseCaseImpl implements CreateRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto apply(CreateRoleRequestDto request, User actor) {
        final String nameUpper = request.getName() == null ? null : request.getName().trim().toUpperCase(Locale.ROOT);
        if (!nonNull(nameUpper) || nameUpper.isEmpty()) {
            throw new BusinessException("Role name is required");
        }
        if (roleRepository.findByName(nameUpper).isPresent()) {
            throw new ConflictException("Role name already exists");
        }
        final Role role = Role.builder()
                .name(nameUpper)
                .description(request.getDescription())
                .roleType(RoleType.OPTIONAL)
                .createdBy(actor)
                .build();
        final Role saved = roleRepository.save(role);
        return toDto(saved);
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
