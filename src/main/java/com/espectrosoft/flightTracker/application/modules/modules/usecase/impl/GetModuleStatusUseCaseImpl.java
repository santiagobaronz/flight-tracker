package com.espectrosoft.flightTracker.application.modules.modules.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.modules.usecase.GetModuleStatusUseCase;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

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
                .map(am -> new ModuleStatusDto(academyId, moduleCode, am.isActive()))
                .orElseGet(() -> new ModuleStatusDto(academyId, moduleCode, false));
    }
}
