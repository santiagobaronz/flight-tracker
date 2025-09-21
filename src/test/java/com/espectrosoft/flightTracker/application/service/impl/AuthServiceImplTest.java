package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;
import com.espectrosoft.flightTracker.application.modules.application.auth.usecase.LoginUseCase;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private LoginUseCase loginUseCase;
    @Mock
    private DomainLookup domainLookup;
    @Mock
    private InternalAccessPolicy internalAccessPolicy;

    private AuthServiceImpl service;

    @Test
    void login_ok_returns_token() {
        service = new AuthServiceImpl(loginUseCase, domainLookup, internalAccessPolicy);
        final LoginRequestDto req = new LoginRequestDto();
        req.setUsername("user1");
        req.setPassword("pass1");

        final User current = User.builder().id(2L).username("user1").academy(Academy.builder().id(1L).name("A").build()).fullName("U").password("x").build();
        when(domainLookup.requireUser("user1")).thenReturn(current);
        final LoginResponseDto expected = new LoginResponseDto("token123");
        when(loginUseCase.apply(eq(req))).thenReturn(expected);

        final LoginResponseDto resp = service.login(req);

        assertEquals("token123", resp.getToken());
        verify(domainLookup, times(1)).requireUser("user1");
        verify(internalAccessPolicy, times(1)).validationForLogin(eq(current));
        verify(loginUseCase, times(1)).apply(eq(req));
    }
}
