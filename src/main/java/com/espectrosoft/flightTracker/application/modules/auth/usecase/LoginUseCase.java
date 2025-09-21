package com.espectrosoft.flightTracker.application.modules.auth.usecase;

import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;

public interface LoginUseCase {
    LoginResponseDto apply(LoginRequestDto request);
}
