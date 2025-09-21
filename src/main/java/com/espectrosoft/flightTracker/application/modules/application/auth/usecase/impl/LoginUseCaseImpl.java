package com.espectrosoft.flightTracker.application.modules.application.auth.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;
import com.espectrosoft.flightTracker.application.modules.application.auth.usecase.LoginUseCase;
import com.espectrosoft.flightTracker.application.security.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoginUseCaseImpl implements LoginUseCase {

    AuthenticationManager authenticationManager;
    JwtService jwtService;

    @Override
    public LoginResponseDto apply(LoginRequestDto request) {
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );
        final Authentication authentication = authenticationManager.authenticate(token);
        final UserDetails principal = (UserDetails) authentication.getPrincipal();
        final String jwt = jwtService.generateToken(principal);
        return new LoginResponseDto(jwt);
    }
}
