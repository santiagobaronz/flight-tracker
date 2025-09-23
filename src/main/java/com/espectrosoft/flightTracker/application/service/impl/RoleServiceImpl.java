package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;
import com.espectrosoft.flightTracker.application.service.RoleService;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.CreateRoleUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.DeleteRoleUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.GetRolePermissionsUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.GetRoleUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.ListRolesUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.UpdateRolePermissionsUseCase;
import com.espectrosoft.flightTracker.application.modules.management.roles.usecase.UpdateRoleUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoleServiceImpl implements RoleService {

    DomainLookup domainLookup;
    ModuleAccessPolicy moduleAccessPolicy;
    CreateRoleUseCase createRoleUseCase;
    ListRolesUseCase listRolesUseCase;
    GetRoleUseCase getRoleUseCase;
    UpdateRoleUseCase updateRoleUseCase;
    DeleteRoleUseCase deleteRoleUseCase;
    GetRolePermissionsUseCase getRolePermissionsUseCase;
    UpdateRolePermissionsUseCase updateRolePermissionsUseCase;

    @Override
    public RoleDto create(CreateRoleRequestDto request) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.CREATE);
        return createRoleUseCase.apply(request, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> list() {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.VIEW);
        return listRolesUseCase.apply();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto get(Long id) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.VIEW);
        return getRoleUseCase.apply(id);
    }

    @Override
    public RoleDto update(Long id, UpdateRoleRequestDto request) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.EDIT);
        return updateRoleUseCase.apply(id, request, currentUser);
    }

    @Override
    public void delete(Long id) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.DELETE);
        deleteRoleUseCase.apply(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionDto> getPermissions(Long id) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.VIEW);
        return getRolePermissionsUseCase.apply(id);
    }

    @Override
    public List<RolePermissionDto> updatePermissions(Long id, UpdateRolePermissionsRequestDto request) {
        final User currentUser = domainLookup.requireCurrentUser();
        final Academy academy = currentUser.getAcademy();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.ROLES, PermissionAction.EDIT);
        return updateRolePermissionsUseCase.apply(id, request);
    }
}
