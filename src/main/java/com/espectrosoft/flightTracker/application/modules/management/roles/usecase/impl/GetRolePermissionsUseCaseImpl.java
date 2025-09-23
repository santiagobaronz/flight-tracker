package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.GetRolePermissionsUseCase;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.RolePermission;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRolePermissionsUseCaseImpl implements GetRolePermissionsUseCase {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public List<RolePermissionDto> apply(Long roleId) {
        final Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
        final List<RolePermission> permissions = rolePermissionRepository.findByRole(role);
        final List<RolePermissionDto> result = new ArrayList<>();
        for (final RolePermission rp : permissions) {
            result.add(RolePermissionDto.builder().moduleCode(rp.getModuleCode()).action(rp.getAction()).build());
        }
        return result;
    }
}
