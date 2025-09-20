package com.espectrosoft.flightTracker.application.usecase;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;

public interface AuthUseCase {
    LoginResponseDto login(LoginRequestDto request);
}
