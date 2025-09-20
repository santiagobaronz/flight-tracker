package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.HourUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourUsageRepository extends JpaRepository<HourUsage, Long> {
}
