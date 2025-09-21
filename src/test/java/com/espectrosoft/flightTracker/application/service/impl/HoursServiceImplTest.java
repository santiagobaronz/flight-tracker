package com.espectrosoft.flightTracker.application.service.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto;
import com.espectrosoft.flightTracker.application.dto.hours.UserAircraftBalanceDto;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.GetBalanceUseCase;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.PurchaseHoursUseCase;
import com.espectrosoft.flightTracker.application.modules.hours.usecase.RegisterUsageUseCase;
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

    private HoursServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HoursServiceImpl(purchaseHoursUseCase, registerUsageUseCase, getBalanceUseCase);
    }

    @Test
    void purchase_delegates_to_usecase() {
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        final PurchaseHoursResponseDto expected = new PurchaseHoursResponseDto(1L, 2.0);
        when(purchaseHoursUseCase.apply(eq(req))).thenReturn(expected);

        final PurchaseHoursResponseDto res = service.purchaseHours(req);

        assertNotNull(res);
        assertEquals(1L, res.getPurchaseId());
        assertEquals(2.0, res.getBalanceHours());
        verify(purchaseHoursUseCase).apply(eq(req));
    }

    @Test
    void register_delegates_to_usecase() {
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        final RegisterUsageResponseDto expected = new RegisterUsageResponseDto(7L, 3.5);
        when(registerUsageUseCase.apply(eq(req))).thenReturn(expected);

        final RegisterUsageResponseDto res = service.registerUsage(req);

        assertNotNull(res);
        assertEquals(7L, res.getUsageId());
        assertEquals(3.5, res.getBalanceHours());
        verify(registerUsageUseCase).apply(eq(req));
    }

    @Test
    void get_balance_delegates_to_usecase() {
        final UserAircraftBalanceDto expected = new UserAircraftBalanceDto(10L, 100L, 5.0, 2.0, 3.0);
        when(getBalanceUseCase.apply(eq(10L), eq(100L))).thenReturn(expected);

        final UserAircraftBalanceDto res = service.getBalance(10L, 100L);

        assertNotNull(res);
        assertEquals(10L, res.getPilotId());
        assertEquals(100L, res.getAircraftId());
        assertEquals(5.0, res.getTotalPurchased());
        assertEquals(2.0, res.getTotalUsed());
        assertEquals(3.0, res.getBalanceHours());
        verify(getBalanceUseCase).apply(eq(10L), eq(100L));
    }
}
