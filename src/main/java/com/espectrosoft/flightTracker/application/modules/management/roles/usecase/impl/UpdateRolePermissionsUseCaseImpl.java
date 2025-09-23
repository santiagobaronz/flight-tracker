package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.UpdateRolePermissionsUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.RolePermission;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class UpdateRolePermissionsUseCaseImpl implements UpdateRolePermissionsUseCase {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public List<RolePermissionDto> apply(Long roleId, UpdateRolePermissionsRequestDto request) {
        final Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
        rolePermissionRepository.deleteByRole(role);

        final Set<String> unique = new HashSet<>();
        final List<RolePermissionDto> result = new ArrayList<>();
        for (final RolePermissionDto dto : request.getPermissions()) {
            if (!nonNull(dto.getModuleCode()) || !nonNull(dto.getAction())) {
                continue;
            }
            final String key = dto.getModuleCode().name() + "|" + dto.getAction().name();
            if (unique.add(key)) {
                final RolePermission rp = RolePermission.builder()
                        .role(role)
                        .moduleCode(dto.getModuleCode())
                        .action(dto.getAction())
                        .build();
                rolePermissionRepository.save(rp);
                result.add(RolePermissionDto.builder().moduleCode(dto.getModuleCode()).action(dto.getAction()).build());
            }
        }
        return result;
    }
}
