package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.GetBalanceUseCase;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.PurchaseHoursUseCase;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.RegisterUsageUseCase;
import com.espectrosoft.flightTracker.application.service.HoursService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HoursServiceImpl implements HoursService {

    PurchaseHoursUseCase purchaseHoursUseCase;
    RegisterUsageUseCase registerUsageUseCase;
    GetBalanceUseCase getBalanceUseCase;

    @Override
    public PurchaseHoursResponseDto purchaseHours(PurchaseHoursRequestDto request) {
        return purchaseHoursUseCase.apply(request);
    }

    @Override
    public RegisterUsageResponseDto registerUsage(RegisterUsageRequestDto request) {
        return registerUsageUseCase.apply(request);
    }

    @Override
    public UserAircraftBalanceDto getBalance(Long pilotId, Long aircraftId) {
        return getBalanceUseCase.apply(pilotId, aircraftId);
    }
}
