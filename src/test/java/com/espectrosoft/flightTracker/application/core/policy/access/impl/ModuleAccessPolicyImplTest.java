package com.espectrosoft.flightTracker.application.core.policy.access.impl;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.application.core.policy.validations.UserActivePolicy;
import com.espectrosoft.flightTracker.application.core.principal.PrincipalService;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import com.espectrosoft.flightTracker.domain.repository.RolePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleAccessPolicyImplTest {

    @Mock private AcademyActivePolicy academyActivePolicy;
    @Mock private UserActivePolicy userActivePolicy;
    @Mock private ModuleEnabledPolicy moduleEnabledPolicy;
    @Mock private RolePermissionRepository rolePermissionRepository;
    @Mock private PrincipalService principalService;

    private ModuleAccessPolicyImpl policy;

    @BeforeEach
    void setUp() {
        policy = new ModuleAccessPolicyImpl(
                academyActivePolicy,
                userActivePolicy,
                moduleEnabledPolicy,
                rolePermissionRepository,
                principalService
        );
    }

    @Test
    void principal_bypass_skips_permission_check() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User user = User.builder().id(99L).username("owner").academy(academy).fullName("Owner").password("x").build();

        when(principalService.isPrincipal(user)).thenReturn(true);

        policy.validate(academy, user, ModuleSection.APPLICATION, ModuleCode.HOURS, PermissionAction.DELETE);

        verify(academyActivePolicy).apply(academy);
        verify(userActivePolicy).apply(user);
        verify(moduleEnabledPolicy).apply(academy, ModuleSection.APPLICATION, ModuleCode.HOURS);
        verify(principalService).isPrincipal(user);
        verifyNoInteractions(rolePermissionRepository);
    }
}
