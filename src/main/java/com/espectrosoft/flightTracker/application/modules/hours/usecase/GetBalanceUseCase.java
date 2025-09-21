package com.espectrosoft.flightTracker.application.modules.hours.usecase;

import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;

public interface GetBalanceUseCase {
    UserAircraftBalanceDto apply(Long pilotId, Long aircraftId);
}
