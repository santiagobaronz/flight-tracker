package com.espectrosoft.flightTracker.application.modules.hours.usecase.impl;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.modules.application.hours.usecase.impl.PurchaseHoursUseCaseImpl;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import com.espectrosoft.flightTracker.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseHoursUseCaseImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private HourPurchaseRepository hourPurchaseRepository;
    @Mock
    private UserAircraftBalanceRepository balanceRepository;

    private PurchaseHoursUseCaseImpl
        useCase;

    @BeforeEach
    void setUp() {
        useCase = new PurchaseHoursUseCaseImpl(
                academyRepository,
                userRepository,
                aircraftRepository,
                hourPurchaseRepository,
                balanceRepository
        );
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("admin", ""));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void purchase_ok_updates_balance_and_persists() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User client = User.builder().id(10L).username("c1").academy(academy).fullName("C").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();
        final User creator = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setClientId(10L);
        req.setAircraftId(100L);
        req.setReceiptNumber("R-1");
        req.setHours(2.0);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(1L)).thenReturn(Optional.of(academy));
        when(userRepository.findById(10L)).thenReturn(Optional.of(client));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft("R-1", aircraft)).thenReturn(false);
        when(hourPurchaseRepository.findByAcademyAndReceiptNumber(academy, "R-1")).thenReturn(List.of());
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(creator));
        when(hourPurchaseRepository.save(any(HourPurchase.class))).thenAnswer(inv -> {
            final HourPurchase p = inv.getArgument(0, HourPurchase.class);
            return HourPurchase.builder()
                    .id(1L)
                    .academy(p.getAcademy())
                    .client(p.getClient())
                    .aircraft(p.getAircraft())
                    .receiptNumber(p.getReceiptNumber())
                    .hours(p.getHours())
                    .purchaseDate(p.getPurchaseDate())
                    .createdBy(p.getCreatedBy())
                    .build();
        });
        when(balanceRepository.findByClientAndAircraft(client, aircraft)).thenReturn(Optional.empty());
        when(balanceRepository.save(any(UserAircraftBalance.class))).thenAnswer(inv -> inv.getArgument(0, UserAircraftBalance.class));

        final PurchaseHoursResponseDto dto = useCase.apply(req);

        assertNotNull(dto);
        assertEquals(1L, dto.getPurchaseId());
        assertEquals(2.0, dto.getBalanceHours());

        verify(academyRepository).findById(1L);
        verify(userRepository).findById(10L);
        verify(aircraftRepository).findById(100L);
        verify(hourPurchaseRepository).existsByReceiptNumberAndAircraft("R-1", aircraft);
        verify(hourPurchaseRepository).findByAcademyAndReceiptNumber(academy, "R-1");
        verify(userRepository).findByUsername("admin");
        verify(hourPurchaseRepository).save(any(HourPurchase.class));
        verify(balanceRepository).findByClientAndAircraft(client, aircraft);
        verify(balanceRepository).save(any(UserAircraftBalance.class));
    }

    @Test
    void duplicate_receipt_for_aircraft_throws_business() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User client = User.builder().id(10L).username("c1").academy(academy).fullName("C").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.AIRCRAFT).build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setClientId(10L);
        req.setAircraftId(100L);
        req.setReceiptNumber("R-1");
        req.setHours(2.0);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(1L)).thenReturn(Optional.of(academy));
        when(userRepository.findById(10L)).thenReturn(Optional.of(client));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft("R-1", aircraft)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.apply(req));

        verify(academyRepository).findById(1L);
        verify(userRepository).findById(10L);
        verify(aircraftRepository).findById(100L);
        verify(hourPurchaseRepository).existsByReceiptNumberAndAircraft("R-1", aircraft);
    }

    @Test
    void receipt_type_inconsistency_throws_business() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User client = User.builder().id(10L).username("c1").academy(academy).fullName("C").password("x").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK-1").model("M").type(AircraftType.SIMULATOR).build();
        final Aircraft prevAircraft = Aircraft.builder().id(200L).academy(academy).registration("HK-2").model("M").type(AircraftType.AIRCRAFT).build();
        final HourPurchase prev = HourPurchase.builder().id(9L).academy(academy).client(client).aircraft(prevAircraft).receiptNumber("R-1").hours(1).build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setClientId(10L);
        req.setAircraftId(100L);
        req.setReceiptNumber("R-1");
        req.setHours(2.0);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(1L)).thenReturn(Optional.of(academy));
        when(userRepository.findById(10L)).thenReturn(Optional.of(client));
        when(aircraftRepository.findById(100L)).thenReturn(Optional.of(aircraft));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft("R-1", aircraft)).thenReturn(false);
        when(hourPurchaseRepository.findByAcademyAndReceiptNumber(academy, "R-1")).thenReturn(List.of(prev));

        assertThrows(BusinessException.class, () -> useCase.apply(req));

        verify(academyRepository).findById(1L);
        verify(userRepository).findById(10L);
        verify(aircraftRepository).findById(100L);
        verify(hourPurchaseRepository).findByAcademyAndReceiptNumber(academy, "R-1");
    }

    @Test
    void missing_entities_throw_not_found() {
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        when(academyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.apply(req));
        verify(academyRepository).findById(1L);
    }
}
