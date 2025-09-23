package com.espectrosoft.flightTracker.application.modules.management.roles.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.roles.CreateRoleRequestDto;
import com.espectrosoft.flightTracker.application.dto.roles.RoleDto;
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
class CreateRoleUseCaseImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CreateRoleUseCaseImpl useCase;

    @Test
    void fails_when_name_missing() {
        final CreateRoleRequestDto req = CreateRoleRequestDto.builder().name(" ").build();
        final User actor = User.builder().id(10L).build();
        assertThrows(BusinessException.class, () -> useCase.apply(req, actor));
        verifyNoInteractions(roleRepository);
    }

    @Test
    void fails_when_name_exists() {
        final CreateRoleRequestDto req = CreateRoleRequestDto.builder().name("Admin").build();
        final User actor = User.builder().id(10L).build();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(Role.builder().id(1L).build()));
        assertThrows(ConflictException.class, () -> useCase.apply(req, actor));
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    void creates_optional_role_uppercase() {
        final CreateRoleRequestDto req = CreateRoleRequestDto.builder().name("mecanico").description("desc").build();
        final User actor = User.builder().id(5L).build();
        when(roleRepository.findByName("MECANICO")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            final Role r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        final RoleDto dto = useCase.apply(req, actor);

        final ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).findByName("MECANICO");
        verify(roleRepository).save(captor.capture());
        final Role saved = captor.getValue();
        assertEquals("MECANICO", saved.getName());
        assertEquals(RoleType.OPTIONAL, saved.getRoleType());
        assertEquals(5L, saved.getCreatedBy().getId());
        assertEquals(99L, dto.getId());
    }
}
