package com.espectrosoft.flightTracker.application.core.principal.impl;

import com.espectrosoft.flightTracker.application.core.principal.PrincipalService;
import com.espectrosoft.flightTracker.domain.model.SystemPrincipal;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.repository.SystemPrincipalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService {

    private final SystemPrincipalRepository systemPrincipalRepository;

    @Override
    public boolean isPrincipal(User user) {
        if (!nonNull(user) || !nonNull(user.getId())) {
            return false;
        }
        return systemPrincipalRepository.findById(1L)
                .map(SystemPrincipal::getUser)
                .map(u -> u.getId().equals(user.getId()))
                .orElse(false);
    }
}
