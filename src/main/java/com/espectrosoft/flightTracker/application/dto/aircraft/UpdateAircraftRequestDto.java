package com.espectrosoft.flightTracker.application.dto.aircraft;

import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAircraftRequestDto {
    @NotBlank
    @Size(max = 20)
    private String registration;

    @NotBlank
    @Size(max = 80)
    private String model;

    @NotNull
    private AircraftType type;
}
