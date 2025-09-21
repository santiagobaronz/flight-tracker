package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;
import com.espectrosoft.flightTracker.application.security.JwtService;
import com.espectrosoft.flightTracker.application.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    AuthenticationManager authenticationManager;
    JwtService jwtService;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(principal);
        return new LoginResponseDto(jwt);
    }
}
