package com.espectrosoft.flightTracker.application.modules.application.hours.usecase;

import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;

public interface RegisterUsageUseCase {
    RegisterUsageResponseDto apply(RegisterUsageRequestDto request);
}
