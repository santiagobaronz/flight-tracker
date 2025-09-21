package com.espectrosoft.flightTracker.application.dto.aircraft;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetActiveRequestDto {
    @NotNull
    private Boolean active;
}
