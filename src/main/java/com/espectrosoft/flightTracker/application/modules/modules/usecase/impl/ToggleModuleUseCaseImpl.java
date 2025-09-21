package com.espectrosoft.flightTracker.application.modules.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.ToggleModuleUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ToggleModuleUseCaseImpl implements ToggleModuleUseCase {

    AcademyRepository academyRepository;
    AcademyModuleRepository academyModuleRepository;

    @Override
    public ModuleStatusDto apply(ModuleToggleRequestDto request) {
        final Academy academy = academyRepository.findById(request.getAcademyId())
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        final AcademyModule am = academyModuleRepository.findByAcademyAndModuleCode(academy, request.getModuleCode())
                .orElseGet(() -> AcademyModule.builder()
                        .academy(academy)
                        .moduleCode(request.getModuleCode())
                        .active(Boolean.FALSE)
                        .build());
        am.setActive(Boolean.TRUE.equals(request.getActive()));
        final AcademyModule saved = academyModuleRepository.save(am);
        final ModuleStatusDto dto = new ModuleStatusDto(academy.getId(), saved.getModuleCode(), saved.isActive());
        dto.setAttributes(saved.getAttributes());
        return dto;
    }
}
