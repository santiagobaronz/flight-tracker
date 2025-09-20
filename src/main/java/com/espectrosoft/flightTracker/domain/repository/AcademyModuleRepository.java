package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.AcademyModule;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademyModuleRepository extends JpaRepository<AcademyModule, Long> {
    Optional<AcademyModule> findByAcademyAndModuleCode(Academy academy, ModuleCode moduleCode);
}
