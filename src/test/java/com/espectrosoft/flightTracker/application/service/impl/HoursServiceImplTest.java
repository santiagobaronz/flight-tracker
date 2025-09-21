package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.GetBalanceUseCase;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.PurchaseHoursUseCase;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.RegisterUsageUseCase;
import com.espectrosoft.flightTracker.application.core.policy.access.ModuleAccessPolicy;
import com.espectrosoft.flightTracker.application.core.lookup.DomainLookup;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HoursServiceImplTest {

    @Mock
    private PurchaseHoursUseCase purchaseHoursUseCase;
    @Mock
    private RegisterUsageUseCase registerUsageUseCase;
    @Mock
    private GetBalanceUseCase getBalanceUseCase;
    @Mock
    private ModuleAccessPolicy moduleAccessPolicy;
    @Mock
    private DomainLookup domainLookup;

    private HoursServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HoursServiceImpl(purchaseHoursUseCase, registerUsageUseCase, getBalanceUseCase,
                moduleAccessPolicy, domainLookup);
    }

    @Test
    void purchase_delegates_to_usecase() {
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final PurchaseHoursResponseDto expected = new PurchaseHoursResponseDto(1L, 2.0);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(purchaseHoursUseCase.apply(eq(req))).thenReturn(expected);

        final PurchaseHoursResponseDto res = service.purchaseHours(req);

        assertNotNull(res);
        assertEquals(1L, res.getPurchaseId());
        assertEquals(2.0, res.getBalanceHours());
        verify(moduleAccessPolicy).validate(eq(academy), eq(current), eq(ModuleCode.HOURS));
        verify(purchaseHoursUseCase).apply(eq(req));
    }

    @Test
    void register_delegates_to_usecase() {
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User current = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final RegisterUsageResponseDto expected = new RegisterUsageResponseDto(7L, 3.5);
        when(domainLookup.requireAcademy(eq(1L))).thenReturn(academy);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(registerUsageUseCase.apply(eq(req))).thenReturn(expected);

        final RegisterUsageResponseDto res = service.registerUsage(req);

        assertNotNull(res);
        assertEquals(7L, res.getUsageId());
        assertEquals(3.5, res.getBalanceHours());
        verify(moduleAccessPolicy).validate(eq(academy), eq(current), eq(ModuleCode.HOURS));
        verify(registerUsageUseCase).apply(eq(req));
    }

    @Test
    void get_balance_delegates_to_usecase() {
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(Academy.builder().id(1L).name("A").build()).registration("HK").model("M").type(AircraftType.AIRCRAFT).build();
        final User current = User.builder().id(2L).username("admin").academy(aircraft.getAcademy()).fullName("Admin").password("p").build();
        final UserAircraftBalanceDto expected = new UserAircraftBalanceDto(10L, 100L, 5.0, 2.0, 3.0);
        when(domainLookup.requireAircraft(eq(100L))).thenReturn(aircraft);
        when(domainLookup.requireCurrentUser()).thenReturn(current);
        when(getBalanceUseCase.apply(eq(10L), eq(100L))).thenReturn(expected);

        final UserAircraftBalanceDto res = service.getBalance(10L, 100L);

        assertNotNull(res);
        assertEquals(10L, res.getPilotId());
        assertEquals(100L, res.getAircraftId());
        assertEquals(5.0, res.getTotalPurchased());
        assertEquals(2.0, res.getTotalUsed());
        assertEquals(3.0, res.getBalanceHours());
        verify(moduleAccessPolicy).validate(eq(aircraft.getAcademy()), eq(current), eq(ModuleCode.HOURS));
        verify(getBalanceUseCase).apply(eq(10L), eq(100L));
    }
}
