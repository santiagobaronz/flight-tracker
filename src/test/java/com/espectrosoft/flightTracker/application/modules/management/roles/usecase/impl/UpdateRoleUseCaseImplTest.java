package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
import com.espectrosoft.flightTracker.application.dto.roles.UpdateRoleRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import com.espectrosoft.flightTracker.domain.model.Role;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.RoleType;
import com.espectrosoft.flightTracker.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRoleUseCaseImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UpdateRoleUseCaseImpl useCase;

    @Test
    void mandatory_role_name_cannot_change() {
        final Role role = Role.builder().id(1L).name("ADMINISTRATOR").roleType(RoleType.MANDATORY).build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        final UpdateRoleRequestDto req = UpdateRoleRequestDto.builder().name("ADMIN").description("d").build();
        final User actor = User.builder().id(50L).build();
        assertThrows(BusinessException.class, () -> useCase.apply(1L, req, actor));
        verify(roleRepository).findById(1L);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void optional_role_rename_conflict() {
        final Role role = Role.builder().id(2L).name("MECANICO").roleType(RoleType.OPTIONAL).build();
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("INSTRUCTOR")).thenReturn(Optional.of(Role.builder().id(3L).build()));
        final UpdateRoleRequestDto req = UpdateRoleRequestDto.builder().name("Instructor").build();
        final User actor = User.builder().id(51L).build();
        assertThrows(ConflictException.class, () -> useCase.apply(2L, req, actor));
        verify(roleRepository).findById(2L);
        verify(roleRepository).findByName("INSTRUCTOR");
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void optional_role_updated_success() {
        final Role role = Role.builder().id(4L).name("MECANICO").roleType(RoleType.OPTIONAL).build();
        when(roleRepository.findById(4L)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("SUP")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));
        final UpdateRoleRequestDto req = UpdateRoleRequestDto.builder().name("sup").description("desc").build();
        final User actor = User.builder().id(60L).build();
        final RoleDto dto = useCase.apply(4L, req, actor);
        final ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        final Role saved = captor.getValue();
        assertEquals("SUP", saved.getName());
        assertEquals("desc", saved.getDescription());
        assertEquals(60L, saved.getUpdatedBy().getId());
        assertEquals(4L, dto.getId());
    }
}
