package com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;

public interface UpdateAircraftUseCase {
    AircraftDto apply(Long id, UpdateAircraftRequestDto request);
}
