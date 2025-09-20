package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.usecase.HoursUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hours")
public class HoursController {

    private final HoursUseCase hoursUseCase;

    public HoursController(HoursUseCase hoursUseCase) {
        this.hoursUseCase = hoursUseCase;
    }

    @PostMapping("/purchase")
    @PreAuthorize("hasAuthority('HOURS_CREATE')")
    public ResponseEntity<PurchaseHoursResponseDto> purchase(@Valid @RequestBody PurchaseHoursRequestDto request) {
        return ResponseEntity.ok(hoursUseCase.purchaseHours(request));
    }

    @PostMapping("/usage")
    @PreAuthorize("hasAuthority('HOURS_EDIT')")
    public ResponseEntity<RegisterUsageResponseDto> registerUsage(@Valid @RequestBody RegisterUsageRequestDto request) {
        return ResponseEntity.ok(hoursUseCase.registerUsage(request));
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('HOURS_VIEW')")
    public ResponseEntity<UserAircraftBalanceDto> getBalance(@RequestParam Long pilotId, @RequestParam Long aircraftId) {
        return ResponseEntity.ok(hoursUseCase.getBalance(pilotId, aircraftId));
    }
}
