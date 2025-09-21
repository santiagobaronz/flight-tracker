package com.espectrosoft.flightTracker.application.service;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;

import java.util.List;

public interface AircraftService {
    List<AircraftDto> listByAcademy(Long academyId);
    List<AircraftDto> listAllByAcademyForManagement(Long academyId);
    AircraftDto update(Long id, UpdateAircraftRequestDto request);
    AircraftDto setActive(Long id, SetActiveRequestDto request);
}
