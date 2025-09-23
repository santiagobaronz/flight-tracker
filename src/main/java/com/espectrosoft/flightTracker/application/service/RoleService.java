package com.espectrosoft.flightTracker.application.service;

import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;

import java.util.List;

public interface RoleService {
    RoleDto create(CreateRoleRequestDto request);
    List<RoleDto> list();
    RoleDto get(Long id);
    RoleDto update(Long id, UpdateRoleRequestDto request);
    void delete(Long id);
    List<RolePermissionDto> getPermissions(Long id);
    List<RolePermissionDto> updatePermissions(Long id, UpdateRolePermissionsRequestDto request);
}
