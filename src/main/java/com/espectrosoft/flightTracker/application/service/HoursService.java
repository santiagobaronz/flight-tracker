package com.espectrosoft.flightTracker.application.service;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;

public interface HoursService {
    PurchaseHoursResponseDto purchaseHours(PurchaseHoursRequestDto request);
    RegisterUsageResponseDto registerUsage(RegisterUsageRequestDto request);
    UserAircraftBalanceDto getBalance(Long clientId, Long aircraftId);
}
