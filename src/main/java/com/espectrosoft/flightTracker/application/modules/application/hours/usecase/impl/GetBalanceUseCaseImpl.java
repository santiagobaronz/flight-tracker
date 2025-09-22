package com.espectrosoft.flightTracker.application.modules.application.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.GetBalanceUseCase;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import com.espectrosoft.flightTracker.domain.repository.UserAircraftBalanceRepository;
import com.espectrosoft.flightTracker.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetBalanceUseCaseImpl implements GetBalanceUseCase {

    UserRepository userRepository;
    AircraftRepository aircraftRepository;
    UserAircraftBalanceRepository balanceRepository;

    @Override
    public UserAircraftBalanceDto apply(Long clientId, Long aircraftId) {
        final User client = userRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        final Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        return balanceRepository.findByClientAndAircraft(client, aircraft)
                .map(b -> new UserAircraftBalanceDto(client.getId(), aircraft.getId(), b.getTotalPurchased(), b.getTotalUsed(), b.getBalanceHours()))
                .orElseGet(() -> new UserAircraftBalanceDto(client.getId(), aircraft.getId(), 0, 0, 0));
    }
}
