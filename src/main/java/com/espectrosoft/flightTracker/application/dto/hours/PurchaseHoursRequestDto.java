package com.espectrosoft.flightTracker.application.dto.hours;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PurchaseHoursRequestDto {
    @NotNull
    private Long academyId;
    @NotNull
    private Long pilotId;
    @NotNull
    private Long aircraftId;
    @NotBlank
    private String receiptNumber;
    @Positive
    private double hours;
    @NotNull
    private LocalDate purchaseDate;
}
