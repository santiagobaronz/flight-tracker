package com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.ListModulesUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ListModulesUseCaseImpl implements ListModulesUseCase {

    AcademyModuleRepository academyModuleRepository;
    DomainLookup domainLookup;

    @Override
    public List<ModuleInfoDto> apply(Long academyId) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        final List<AcademyModule> list = academyModuleRepository.findByAcademy(academy);
        return list.stream().map(am -> ModuleInfoDto.builder()
                .academyId(academyId)
                .section(am.getSection())
                .moduleCode(am.getModuleCode())
                .active(am.isActive())
                .name(am.getName())
                .description(am.getDescription())
                .route(am.getRoute())
                .attributes(am.getAttributes())
                .build()).collect(Collectors.toList());
    }
}
