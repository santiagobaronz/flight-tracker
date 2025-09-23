package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RolePermissionDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.RolePermission;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRolePermissionsUseCaseImplTest {

    @Mock private RoleRepository roleRepository;
    @Mock private RolePermissionRepository rolePermissionRepository;

    @InjectMocks private GetRolePermissionsUseCaseImpl useCase;

    @Test
    void not_found_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(1L));
        verify(roleRepository).findById(1L);
        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    void returns_permissions() {
        final Role role = Role.builder().id(2L).build();
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        final RolePermission p1 = RolePermission.builder().moduleCode(ModuleCode.HOURS).action(PermissionAction.VIEW).role(role).build();
        final RolePermission p2 = RolePermission.builder().moduleCode(ModuleCode.AIRCRAFT).action(PermissionAction.EDIT).role(role).build();
        when(rolePermissionRepository.findByRole(role)).thenReturn(Arrays.asList(p1, p2));
        final List<RolePermissionDto> list = useCase.apply(2L);
        assertEquals(2, list.size());
        verify(roleRepository).findById(2L);
        verify(rolePermissionRepository).findByRole(role);
    }
}
