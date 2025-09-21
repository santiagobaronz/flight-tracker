package com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;

import java.util.List;

public interface ListAircraftUseCase {
    List<AircraftDto> apply(Long academyId, boolean onlyActive);
}
