package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.enums.RoleType;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRoleUseCaseImplTest {

    @Mock private RoleRepository roleRepository;
    @InjectMocks private DeleteRoleUseCaseImpl useCase;

    @Test
    void not_found_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(1L));
        verify(roleRepository).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void mandatory_cannot_delete() {
        final Role role = Role.builder().id(2L).roleType(RoleType.MANDATORY).build();
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        assertThrows(BusinessException.class, () -> useCase.apply(2L));
        verify(roleRepository).findById(2L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void has_assignments_conflict() {
        final Role role = Role.builder().id(3L).roleType(RoleType.OPTIONAL).build();
        when(roleRepository.findById(3L)).thenReturn(Optional.of(role));
        when(roleRepository.countUsersByRoleId(3L)).thenReturn(2L);
        assertThrows(ConflictException.class, () -> useCase.apply(3L));
        verify(roleRepository).findById(3L);
        verify(roleRepository).countUsersByRoleId(3L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void delete_success() {
        final Role role = Role.builder().id(4L).roleType(RoleType.OPTIONAL).build();
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));
        when(roleRepository.countUsersByRoleId(4L)).thenReturn(0L);
        useCase.apply(4L);
        verify(roleRepository).findById(4L);
        verify(roleRepository).countUsersByRoleId(4L);
        verify(roleRepository).delete(role);
    }
}
