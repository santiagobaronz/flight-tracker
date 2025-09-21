package com.espectrosoft.flightTracker.application.modules.management.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.management.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetModuleStatusUseCaseImpl implements GetModuleStatusUseCase {

    AcademyRepository academyRepository;
    AcademyModuleRepository academyModuleRepository;

    @Override
    public ModuleStatusDto apply(Long academyId, ModuleCode moduleCode) {
        final Academy academy = academyRepository.findById(academyId)
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        return academyModuleRepository.findByAcademyAndModuleCode(academy, moduleCode)
                .map(am -> {
                    final ModuleStatusDto dto = new ModuleStatusDto(academyId, moduleCode, am.isActive());
                    dto.setAttributes(am.getAttributes());
                    return dto;
                })
                .orElseGet(() -> {
                    final ModuleStatusDto dto = new ModuleStatusDto(academyId, moduleCode, false);
                    dto.setAttributes(Collections.emptyList());
                    return dto;
                });
    }
}
