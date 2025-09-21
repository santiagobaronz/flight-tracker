package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
import com.espectrosoft.flightTracker.application.service.AircraftService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AircraftDto>> list(@RequestParam Long academyId) {
        return ResponseEntity.ok(aircraftService.listByAcademy(academyId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AircraftDto> update(@PathVariable Long id, @Valid @RequestBody UpdateAircraftRequestDto request) {
        return ResponseEntity.ok(aircraftService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AircraftDto> setActive(@PathVariable Long id, @Valid @RequestBody SetActiveRequestDto request) {
        return ResponseEntity.ok(aircraftService.setActive(id, request));
    }
}
