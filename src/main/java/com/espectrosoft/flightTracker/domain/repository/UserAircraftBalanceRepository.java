package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.UserAircraftBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAircraftBalanceRepository extends JpaRepository<UserAircraftBalance, Long> {
    Optional<UserAircraftBalance> findByPilotAndAircraft(User pilot, Aircraft aircraft);
}
