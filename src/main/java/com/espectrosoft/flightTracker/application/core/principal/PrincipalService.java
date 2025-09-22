package com.espectrosoft.flightTracker.application.core.principal;

import com.espectrosoft.flightTracker.domain.model.User;

public interface PrincipalService {
    boolean isPrincipal(User user);
}
