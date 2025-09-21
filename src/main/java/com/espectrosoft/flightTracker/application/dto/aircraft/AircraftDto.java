package com.espectrosoft.flightTracker.application.dto.aircraft;

import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftDto {
    private Long id;
    private Long academyId;
    private String registration;
    private String model;
    private AircraftType type;
    private boolean active;
}
