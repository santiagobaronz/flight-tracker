package com.espectrosoft.flightTracker.application.modules.application.hours.usecase;

import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;

public interface GetBalanceUseCase {
    UserAircraftBalanceDto apply(Long clientId, Long aircraftId);
}
