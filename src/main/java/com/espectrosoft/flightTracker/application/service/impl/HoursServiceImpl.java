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
import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
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
    ModuleAccessPolicy moduleAccessPolicy;
    DomainLookup domainLookup;

    @Override
    public PurchaseHoursResponseDto purchaseHours(PurchaseHoursRequestDto request) {
        final Academy academy = domainLookup.requireAcademy(request.getAcademyId());
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(academy, currentUser, ModuleCode.HOURS);
        return purchaseHoursUseCase.apply(request);
    }

    @Override
    public RegisterUsageResponseDto registerUsage(RegisterUsageRequestDto request) {
        final Academy academy = domainLookup.requireAcademy(request.getAcademyId());
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(academy, currentUser, ModuleCode.HOURS);
        return registerUsageUseCase.apply(request);
    }

    @Override
    public UserAircraftBalanceDto getBalance(Long pilotId, Long aircraftId) {
        final Aircraft aircraft = domainLookup.requireAircraft(aircraftId);
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(aircraft.getAcademy(), currentUser, ModuleCode.HOURS);
        return getBalanceUseCase.apply(pilotId, aircraftId);
    }
}
