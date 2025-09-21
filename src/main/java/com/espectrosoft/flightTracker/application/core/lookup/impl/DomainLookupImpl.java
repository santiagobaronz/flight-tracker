package com.espectrosoft.flightTracker.application.core.lookup.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.util.SecurityUtil;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import com.espectrosoft.flightTracker.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DomainLookupImpl implements DomainLookup {

    AcademyRepository academyRepository;
    AircraftRepository aircraftRepository;
    UserRepository userRepository;

    @Override
    public Academy requireAcademy(Long academyId) {
        return academyRepository.findById(academyId)
                .orElseThrow(() -> new NotFoundException("Academy not found"));
    }

    @Override
    public Aircraft requireAircraft(Long aircraftId) {
        return aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
    }

    @Override
    public User requireCurrentUser() {
        final String username = SecurityUtil.currentUsername();
        if (username == null) {
            throw new NotFoundException("User not found");
        }
        return requireUser(username);
    }

    @Override
    public User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
