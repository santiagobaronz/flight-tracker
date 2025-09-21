package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.core.policy.access.PublicAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.service.AircraftService;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AircraftServiceImpl implements AircraftService {

    AircraftRepository aircraftRepository;
    DomainLookup domainLookup;
    PublicAccessPolicy publicAccessPolicy;
    InternalAccessPolicy internalAccessPolicy;

    @Override
    public List<AircraftDto> listByAcademy(Long academyId) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final User current = domainLookup.requireCurrentUser();
        if (!Objects.equals(current.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("User not in academy");
        }
        publicAccessPolicy.validate(academy);
        return aircraftRepository.findByAcademy(academy)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AircraftDto update(Long id, UpdateAircraftRequestDto request) {
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

    @Override
    public AircraftDto setActive(Long id, SetActiveRequestDto request) {
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
