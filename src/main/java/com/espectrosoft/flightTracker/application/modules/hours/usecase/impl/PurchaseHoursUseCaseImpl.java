package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.core.policy.ModuleEnabledPolicy;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.PurchaseHoursUseCase;
import com.espectrosoft.flightTracker.application.exception.BusinessException;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.util.SecurityUtil;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PurchaseHoursUseCaseImpl implements PurchaseHoursUseCase {

    AcademyRepository academyRepository;
    UserRepository userRepository;
    AircraftRepository aircraftRepository;
    HourPurchaseRepository hourPurchaseRepository;
    UserAircraftBalanceRepository balanceRepository;
    ModuleEnabledPolicy moduleEnabledPolicy;

    @Override
    public PurchaseHoursResponseDto apply(PurchaseHoursRequestDto request) {
        final Academy academy = academyRepository.findById(request.getAcademyId())
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        moduleEnabledPolicy.apply(academy, ModuleCode.HOURS);

        final User pilot = userRepository.findById(request.getPilotId())
                .orElseThrow(() -> new NotFoundException("Pilot not found"));
        if (!Objects.equals(pilot.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Pilot not in academy");
        }

        final Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        if (!Objects.equals(aircraft.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Aircraft not in academy");
        }

        if (hourPurchaseRepository.existsByReceiptNumberAndAircraft(request.getReceiptNumber(), aircraft)) {
            throw new BusinessException("Duplicate receipt for aircraft");
        }

        final List<HourPurchase> sameReceipt = hourPurchaseRepository.findByAcademyAndReceiptNumber(academy, request.getReceiptNumber());
        if (!sameReceipt.isEmpty()) {
            final String expectedType = sameReceipt.get(0).getAircraft().getType();
            if (!Objects.equals(expectedType, aircraft.getType())) {
                throw new BusinessException("Receipt must be for same aircraft type");
            }
        }

        final String currentUsername = SecurityUtil.currentUsername();
        final User createdBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Creator user not found"));

        final HourPurchase purchase = HourPurchase.builder()
                .academy(academy)
                .pilot(pilot)
                .aircraft(aircraft)
                .receiptNumber(request.getReceiptNumber())
                .hours(request.getHours())
                .purchaseDate(request.getPurchaseDate())
                .createdBy(createdBy)
                .build();
        final HourPurchase saved = hourPurchaseRepository.save(purchase);

        final UserAircraftBalance balance = balanceRepository.findByPilotAndAircraft(pilot, aircraft)
                .orElseGet(() -> UserAircraftBalance.builder()
                        .pilot(pilot)
                        .aircraft(aircraft)
                        .totalPurchased(0)
                        .totalUsed(0)
                        .balanceHours(0)
                        .build());
        balance.setTotalPurchased(balance.getTotalPurchased() + request.getHours());
        balance.setBalanceHours(balance.getBalanceHours() + request.getHours());
        balanceRepository.save(balance);

        return new PurchaseHoursResponseDto(saved.getId(), balance.getBalanceHours());
    }
}
