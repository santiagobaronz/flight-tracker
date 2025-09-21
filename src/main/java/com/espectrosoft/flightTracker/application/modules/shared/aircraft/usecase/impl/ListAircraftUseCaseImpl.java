package com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.PublicAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase.ListAircraftUseCase;
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
public class ListAircraftUseCaseImpl implements ListAircraftUseCase {

    AircraftRepository aircraftRepository;
    DomainLookup domainLookup;
    PublicAccessPolicy publicAccessPolicy;

    @Override
    public List<AircraftDto> apply(Long academyId, boolean onlyActive) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final User current = domainLookup.requireCurrentUser();
        if (!Objects.equals(current.getAcademy().getId(), academy.getId())) {
            throw new BusinessException("User not in academy");
        }
        publicAccessPolicy.validate(academy);
        final List<Aircraft> list = onlyActive
                ? aircraftRepository.findByAcademyAndIsActiveTrue(academy)
                : aircraftRepository.findByAcademy(academy);
        return list.stream().map(this::toDto).collect(Collectors.toList());
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
