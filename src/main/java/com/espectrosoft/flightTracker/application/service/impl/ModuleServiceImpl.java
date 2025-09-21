package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.module.ModuleStatusDto;
import com.espectrosoft.flightTracker.application.dto.module.ModuleToggleRequestDto;
import com.espectrosoft.flightTracker.application.exception.NotFoundException;
import com.espectrosoft.flightTracker.application.service.ModuleService;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.AcademyModuleRepository;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ModuleServiceImpl implements ModuleService {

    AcademyRepository academyRepository;
    AcademyModuleRepository academyModuleRepository;

    @Override
    public ModuleStatusDto toggle(ModuleToggleRequestDto request) {
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
        return new ModuleStatusDto(academy.getId(), saved.getModuleCode(), saved.isActive());
    }

    @Override
    public ModuleStatusDto status(Long academyId, ModuleCode moduleCode) {
        final Academy academy = academyRepository.findById(academyId)
                .orElseThrow(() -> new NotFoundException("Academy not found"));
        return academyModuleRepository.findByAcademyAndModuleCode(academy, moduleCode)
                .map(am -> new ModuleStatusDto(academyId, moduleCode, am.isActive()))
                .orElseGet(() -> new ModuleStatusDto(academyId, moduleCode, false));
    }
}
