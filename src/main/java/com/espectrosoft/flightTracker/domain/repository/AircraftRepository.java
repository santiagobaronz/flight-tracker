package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.Academy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
    Optional<Aircraft> findByAcademyAndRegistration(Academy academy, String registration);
    List<Aircraft> findByAcademy(Academy academy);
    List<Aircraft> findByAcademyAndIsActiveTrue(Academy academy);
}
