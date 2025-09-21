package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.RegisterUsageUseCase;
import com.espectrosoft.flightTracker.application.util.SecurityUtil;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterUsageUseCaseImpl implements RegisterUsageUseCase {

    AcademyRepository academyRepository;
    UserRepository userRepository;
    AircraftRepository aircraftRepository;
    HourUsageRepository hourUsageRepository;
    UserAircraftBalanceRepository balanceRepository;

    @Override
    public RegisterUsageResponseDto apply(RegisterUsageRequestDto request) {
        final Academy academy = academyRepository.findById(request.getAcademyId())
                .orElseThrow(() -> new NotFoundException("Academy not found"));

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
}
