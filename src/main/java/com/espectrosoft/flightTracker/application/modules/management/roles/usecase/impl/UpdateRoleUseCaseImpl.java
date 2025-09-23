package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.UpdateRoleUseCase;
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
public class UpdateRoleUseCaseImpl implements UpdateRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto apply(Long id, UpdateRoleRequestDto request, User actor) {
        final Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
        if (role.getRoleType() == RoleType.MANDATORY) {
            if (nonNull(request.getName()) && !role.getName().equalsIgnoreCase(request.getName().trim())) {
                throw new BusinessException("Mandatory role name cannot be modified");
            }
            role.setDescription(request.getDescription());
        } else {
            if (nonNull(request.getName())) {
                final String nameUpper = request.getName().trim().toUpperCase(Locale.ROOT);
                if (!role.getName().equals(nameUpper) && roleRepository.findByName(nameUpper).isPresent()) {
                    throw new ConflictException("Role name already exists");
                }
                role.setName(nameUpper);
            }
            role.setDescription(request.getDescription());
        }
        role.setUpdatedBy(actor);
        final Role saved = roleRepository.save(role);
        final Long createdById = saved.getCreatedBy() != null ? saved.getCreatedBy().getId() : null;
        final Long updatedById = saved.getUpdatedBy() != null ? saved.getUpdatedBy().getId() : null;
        return RoleDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .roleType(saved.getRoleType())
                .createdById(createdById)
                .updatedById(updatedById)
                .build();
    }
}
