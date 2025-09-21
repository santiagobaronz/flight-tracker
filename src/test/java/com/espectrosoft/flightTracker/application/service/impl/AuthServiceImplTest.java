package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;
import com.espectrosoft.flightTracker.application.modules.auth.usecase.LoginUseCase;
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

    private AuthServiceImpl service;

    @Test
    void login_ok_returns_token() {
        service = new AuthServiceImpl(loginUseCase);
        final LoginRequestDto req = new LoginRequestDto();
        req.setUsername("user1");
        req.setPassword("pass1");

        final LoginResponseDto expected = new LoginResponseDto("token123");
        when(loginUseCase.apply(eq(req))).thenReturn(expected);

        final LoginResponseDto resp = service.login(req);

        assertEquals("token123", resp.getToken());
        verify(loginUseCase, times(1)).apply(eq(req));
    }
}
