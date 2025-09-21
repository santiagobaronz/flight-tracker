package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.exception.BusinessException;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.exception.ModuleDisabledException;
import com.espectrosoft.flightTracker.application.service.HoursService;
import com.espectrosoft.flightTracker.application.util.SecurityUtil;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HoursServiceImpl implements HoursService {

    AcademyRepository academyRepository;
    UserRepository userRepository;
    AircraftRepository aircraftRepository;
    HourPurchaseRepository hourPurchaseRepository;
    HourUsageRepository hourUsageRepository;
    UserAircraftBalanceRepository balanceRepository;
    AcademyModuleRepository academyModuleRepository;

    @Override
    public PurchaseHoursResponseDto purchaseHours(PurchaseHoursRequestDto request) {
        final Academy academy = academyRepository.findById(request.getAcademyId())
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        ensureModuleActive(academy, ModuleCode.HOURS);

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

    @Override
    public RegisterUsageResponseDto registerUsage(RegisterUsageRequestDto request) {
        final Academy academy = academyRepository.findById(request.getAcademyId())
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        ensureModuleActive(academy, ModuleCode.HOURS);

        final User pilot = userRepository.findById(request.getPilotId())
                .orElseThrow(() -> new NotFoundException("Pilot not found"));
        if (!Objects.equals(pilot.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Pilot not in academy");
        }

        final User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new NotFoundException("Instructor not found"));
        if (!Objects.equals(instructor.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Instructor not in academy");
        }

        final Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        if (!Objects.equals(aircraft.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Aircraft not in academy");
        }

        final UserAircraftBalance balance = balanceRepository.findByPilotAndAircraft(pilot, aircraft)
                .orElseThrow(() -> new BusinessException("No balance for pilot and aircraft"));
        if (request.getHours() > balance.getBalanceHours()) {
            throw new BusinessException("Insufficient hours balance");
        }

        final String currentUsername = SecurityUtil.currentUsername();
        final User createdBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Creator user not found"));

        final HourUsage usage = HourUsage.builder()
                .academy(academy)
                .pilot(pilot)
                .aircraft(aircraft)
                .instructor(instructor)
                .hours(request.getHours())
                .flightDate(request.getFlightDate())
                .logbookNumber(request.getLogbookNumber())
                .createdBy(createdBy)
                .build();
        final HourUsage saved = hourUsageRepository.save(usage);

        balance.setTotalUsed(balance.getTotalUsed() + request.getHours());
        balance.setBalanceHours(balance.getBalanceHours() - request.getHours());
        balanceRepository.save(balance);

        return new RegisterUsageResponseDto(saved.getId(), balance.getBalanceHours());
    }

    @Override
    public UserAircraftBalanceDto getBalance(Long pilotId, Long aircraftId) {
        final User pilot = userRepository.findById(pilotId)
                .orElseThrow(() -> new NotFoundException("Pilot not found"));
        final Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        ensureModuleActive(aircraft.getAcademy(), ModuleCode.HOURS);
        return balanceRepository.findByPilotAndAircraft(pilot, aircraft)
                .map(b -> new UserAircraftBalanceDto(pilot.getId(), aircraft.getId(), b.getTotalPurchased(), b.getTotalUsed(), b.getBalanceHours()))
                .orElseGet(() -> new UserAircraftBalanceDto(pilot.getId(), aircraft.getId(), 0, 0, 0));
    }

    private void ensureModuleActive(Academy academy, ModuleCode module) {
        academyModuleRepository.findByAcademyAndModuleCode(academy, module)
                .filter(AcademyModule::isActive)
                .orElseThrow(() -> new ModuleDisabledException("Module is disabled for this academy"));
    }
}
