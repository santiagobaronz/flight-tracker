package com.espectrosoft.flightTracker.application.dto.hours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseHoursResponseDto {
    private Long purchaseId;
    private double balanceHours;
}
