package com.espectrosoft.flightTracker.application.dto.hours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterUsageResponseDto {
    private Long usageId;
    private double balanceHours;
}
