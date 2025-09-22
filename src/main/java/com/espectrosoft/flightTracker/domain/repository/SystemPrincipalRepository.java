package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.SystemPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPrincipalRepository extends JpaRepository<SystemPrincipal, Long> {
}
