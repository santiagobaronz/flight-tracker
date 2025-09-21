package com.espectrosoft.flightTracker.application.modules.hours.usecase;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;

public interface PurchaseHoursUseCase {
    PurchaseHoursResponseDto apply(PurchaseHoursRequestDto request);
}
