package com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.SetAircraftActiveUseCase;
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
public class SetAircraftActiveUseCaseImpl implements SetAircraftActiveUseCase {

    AircraftRepository aircraftRepository;
    DomainLookup domainLookup;
    InternalAccessPolicy internalAccessPolicy;

    @Override
    public AircraftDto apply(Long id, SetActiveRequestDto request) {
        final Aircraft aircraft = domainLookup.requireAircraft(id);
        final Academy academy = aircraft.getAcademy();
        final User current = domainLookup.requireCurrentUser();
        if (!Objects.equals(current.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("Aircraft not in academy");
        }
        internalAccessPolicy.validate(academy, current);
        aircraft.setActive(Boolean.TRUE.equals(request.getActive()));
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
