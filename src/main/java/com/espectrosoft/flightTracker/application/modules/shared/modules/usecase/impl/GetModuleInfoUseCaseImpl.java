package com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.dto.module.ModuleInfoDto;
import com.espectrosoft.flightTracker.application.modules.shared.modules.usecase.GetModuleInfoUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleSection;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetModuleInfoUseCaseImpl implements GetModuleInfoUseCase {

    AcademyModuleRepository academyModuleRepository;
    DomainLookup domainLookup;

    @Override
    public ModuleInfoDto apply(Long academyId, ModuleSection section, ModuleCode moduleCode) {
        final Academy academy = domainLookup.requireAcademy(academyId);
        return academyModuleRepository.findByAcademyAndSectionAndModuleCode(academy, section, moduleCode)
                .map(this::toDto)
                .orElseGet(() -> ModuleInfoDto.builder()
                        .academyId(academyId)
                        .section(section)
                        .moduleCode(moduleCode)
                        .active(false)
                        .build());
    }

    private ModuleInfoDto toDto(AcademyModule am) {
        return ModuleInfoDto.builder()
                .academyId(am.getAcademy().getId())
                .section(am.getSection())
                .moduleCode(am.getModuleCode())
                .active(am.isActive())
                .name(am.getName())
                .description(am.getDescription())
                .route(am.getRoute())
                .attributes(am.getAttributes())
                .build();
    }
}
