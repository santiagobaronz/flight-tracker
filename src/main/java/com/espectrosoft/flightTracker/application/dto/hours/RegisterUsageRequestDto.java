package com.espectrosoft.flightTracker.application.dto.hours;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterUsageRequestDto {
    @NotNull
    private Long academyId;
    @NotNull
    private Long pilotId;
    @NotNull
    private Long aircraftId;
    @NotNull
    private Long instructorId;
    @Positive
    private double hours;
    @NotNull
    private LocalDate flightDate;
    @NotBlank
    private String logbookNumber;
}
