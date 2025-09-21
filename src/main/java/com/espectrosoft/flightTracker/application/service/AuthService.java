package com.espectrosoft.flightTracker.application.service;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}
