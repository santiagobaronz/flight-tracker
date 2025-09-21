package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.aircraft.AircraftDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.SetActiveRequestDto;
import com.espectrosoft.flightTracker.application.dto.aircraft.UpdateAircraftRequestDto;
import com.espectrosoft.flightTracker.application.service.AircraftService;
import com.espectrosoft.flightTracker.application.modules.shared.aircraft.usecase.ListAircraftUseCase;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.UpdateAircraftUseCase;
import com.espectrosoft.flightTracker.application.modules.management.aircraft.usecase.SetAircraftActiveUseCase;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.model.enums.PermissionAction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AircraftServiceImpl implements AircraftService {

    ListAircraftUseCase listAircraftUseCase;
    UpdateAircraftUseCase updateAircraftUseCase;
    SetAircraftActiveUseCase setAircraftActiveUseCase;
    ModuleAccessPolicy moduleAccessPolicy;
    DomainLookup domainLookup;

    @Override
    public List<AircraftDto> listByAcademy(Long academyId) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.APPLICATION, ModuleCode.AIRCRAFT, PermissionAction.VIEW);
        return listAircraftUseCase.apply(academyId, true);
    }

    @Override
    public List<AircraftDto> listAllByAcademyForManagement(Long academyId) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(academy, currentUser, ModuleSection.MANAGEMENT, ModuleCode.AIRCRAFT, PermissionAction.VIEW);
        return listAircraftUseCase.apply(academyId, false);
    }

    @Override
    public AircraftDto update(Long id, UpdateAircraftRequestDto request) {
        final Aircraft aircraft = domainLookup.requireAircraft(id);
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(aircraft.getAcademy(), currentUser, ModuleSection.MANAGEMENT, ModuleCode.AIRCRAFT, PermissionAction.EDIT);
        return updateAircraftUseCase.apply(id, request);
    }

    @Override
    public AircraftDto setActive(Long id, SetActiveRequestDto request) {
        final Aircraft aircraft = domainLookup.requireAircraft(id);
        final User currentUser = domainLookup.requireCurrentUser();
        moduleAccessPolicy.validate(aircraft.getAcademy(), currentUser, ModuleSection.MANAGEMENT, ModuleCode.AIRCRAFT, PermissionAction.EDIT);
        return setAircraftActiveUseCase.apply(id, request);
    }
}
