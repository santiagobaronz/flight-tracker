package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRolePermissionsRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.RolePermission;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRolePermissionsUseCaseImplTest {

    @Mock private RoleRepository roleRepository;
    @Mock private RolePermissionRepository rolePermissionRepository;

    @InjectMocks private UpdateRolePermissionsUseCaseImpl useCase;

    @Test
    void not_found_role() {
        when(roleRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(100L, new UpdateRolePermissionsRequestDto()));
        verify(roleRepository).findById(100L);
        verifyNoMoreInteractions(roleRepository);
        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    void replaces_permissions_unique_only() {
        final Role role = Role.builder().id(5L).build();
        when(roleRepository.findById(5L)).thenReturn(Optional.of(role));

        final UpdateRolePermissionsRequestDto req = UpdateRolePermissionsRequestDto.builder()
                .permissions(Arrays.asList(
                        RolePermissionDto.builder().moduleCode(ModuleCode.HOURS).action(PermissionAction.VIEW).build(),
                        RolePermissionDto.builder().moduleCode(ModuleCode.HOURS).action(PermissionAction.VIEW).build(),
                        RolePermissionDto.builder().moduleCode(ModuleCode.AIRCRAFT).action(PermissionAction.EDIT).build(),
                        RolePermissionDto.builder().moduleCode(null).action(PermissionAction.EDIT).build()
                ))
                .build();

        when(rolePermissionRepository.save(any(RolePermission.class))).thenAnswer(inv -> inv.getArgument(0));

        final List<RolePermissionDto> out = useCase.apply(5L, req);

        verify(roleRepository).findById(5L);
        verify(rolePermissionRepository).deleteByRole(role);
        final ArgumentCaptor<RolePermission> captor = ArgumentCaptor.forClass(RolePermission.class);
        verify(rolePermissionRepository, times(2)).save(captor.capture());
        final List<RolePermission> saved = captor.getAllValues();
        assertEquals(2, saved.size());
        assertTrue(saved.stream().anyMatch(rp -> rp.getModuleCode() == ModuleCode.HOURS && rp.getAction() == PermissionAction.VIEW));
        assertTrue(saved.stream().anyMatch(rp -> rp.getModuleCode() == ModuleCode.AIRCRAFT && rp.getAction() == PermissionAction.EDIT));
        assertEquals(2, out.size());
    }
}
