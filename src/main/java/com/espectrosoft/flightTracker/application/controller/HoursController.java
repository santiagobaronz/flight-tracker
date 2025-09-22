package com.espectrosoft.flightTracker.application.controller;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.service.HoursService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hours")
public class HoursController {

    private final HoursService hoursService;

    public HoursController(HoursService hoursService) {
        this.hoursService =
            hoursService;
    }

    @PostMapping("/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PurchaseHoursResponseDto> purchase(@Valid @RequestBody PurchaseHoursRequestDto request) {
        return ResponseEntity.ok(hoursService.purchaseHours(request));
    }

    @PostMapping("/usage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RegisterUsageResponseDto> registerUsage(@Valid @RequestBody RegisterUsageRequestDto request) {
        return ResponseEntity.ok(hoursService.registerUsage(request));
    }

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAircraftBalanceDto> getBalance(@RequestParam Long clientId, @RequestParam Long aircraftId) {
        return ResponseEntity.ok(hoursService.getBalance(clientId, aircraftId));
    }
}
