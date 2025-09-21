package com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.UpdateAircraftUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateAircraftUseCaseImpl implements UpdateAircraftUseCase {

    AircraftRepository aircraftRepository;
    DomainLookup domainLookup;
    InternalAccessPolicy internalAccessPolicy;

    @Override
    public AircraftDto apply(Long id, UpdateAircraftRequestDto request) {

        final Aircraft aircraft = domainLookup.requireAircraft(id);
        final Academy academy = aircraft.getAcademy();
        final User current = domainLookup.requireCurrentUser();
        if (!Objects.equals(current.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Aircraft not in academy");
        }

        internalAccessPolicy.validate(academy, current);

        if (!aircraft.getRegistration().equals(request.getRegistration())) {
            aircraftRepository.findByAcademyAndRegistration(academy, request.getRegistration())
                    .ifPresent(existing -> {
                        if (!Objects.equals(existing.getId(), aircraft.getId())) {
                            throw new BusinessException("Duplicate registration");
                        }
                    });
        }

        aircraft.setRegistration(request.getRegistration());
        aircraft.setModel(request.getModel());
        aircraft.setType(request.getType());
        final Aircraft saved = aircraftRepository.save(aircraft);

        return toDto(saved);
    }

    private AircraftDto toDto(Aircraft a) {
        return AircraftDto.builder()
                .id(a.getId())
                .academyId(a.getAcademy().getId())
                .registration(a.getRegistration())
                .model(a.getModel())
                .type(a.getType())
                .active(a.isActive())
                .build();
    }
}
