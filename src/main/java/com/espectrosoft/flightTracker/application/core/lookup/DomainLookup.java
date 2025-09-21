package com.espectrosoft.flightTracker.application.core.lookup;

import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;

public interface DomainLookup {
    Academy requireAcademy(Long academyId);
    Aircraft requireAircraft(Long aircraftId);
    User requireCurrentUser();
    User requireUser(String username);
}
