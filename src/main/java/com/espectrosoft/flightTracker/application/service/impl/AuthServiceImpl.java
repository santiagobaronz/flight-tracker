package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.application.core.policy.access.InternalAccessPolicy;
import com.espectrosoft.flightTracker.application.dto.auth.LoginRequestDto;
import com.espectrosoft.flightTracker.application.dto.auth.LoginResponseDto;
import com.espectrosoft.flightTracker.application.modules.application.auth.usecase.LoginUseCase;
import com.espectrosoft.flightTracker.application.service.AuthService;
import com.espectrosoft.flightTracker.domain.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    LoginUseCase loginUseCase;
    DomainLookup domainLookup;
    InternalAccessPolicy internalAccessPolicy;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        final User currentUser = domainLookup.requireUser(request.getUsername());
        internalAccessPolicy.validationForLogin(currentUser);
        return loginUseCase.apply(request);
    }
}
