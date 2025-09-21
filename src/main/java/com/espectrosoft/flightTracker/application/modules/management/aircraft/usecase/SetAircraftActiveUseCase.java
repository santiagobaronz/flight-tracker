package com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;

public interface SetAircraftActiveUseCase {
    AircraftDto apply(Long id, SetActiveRequestDto request);
}
