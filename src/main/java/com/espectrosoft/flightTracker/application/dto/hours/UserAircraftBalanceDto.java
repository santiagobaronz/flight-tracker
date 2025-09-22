package com.espectrosoft.flightTracker.application.dto.hours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAircraftBalanceDto {
    private Long clientId;
    private Long aircraftId;
    private double totalPurchased;
    private double totalUsed;
    private double balanceHours;
}
