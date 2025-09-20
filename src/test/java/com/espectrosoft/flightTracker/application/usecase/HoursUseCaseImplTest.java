package com.espectrosoft.flightTracker.application.usecase;

import com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursRequestDto;
import com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageRequestDto;
import com.espectrosoft.flightTracker.application.exception.BusinessException;
import com.espectrosoft.flightTracker.application.usecase.impl.HoursUseCaseImpl;
import com.espectrosoft.flightTracker.domain.model.*;
import com.espectrosoft.flightTracker.domain.model.enums.ModuleCode;
import com.espectrosoft.flightTracker.domain.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HoursUseCaseImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private HourPurchaseRepository hourPurchaseRepository;
    @Mock
    private HourUsageRepository hourUsageRepository;
    @Mock
    private UserAircraftBalanceRepository balanceRepository;
    @Mock
    private AcademyModuleRepository academyModuleRepository;

    @InjectMocks
    private HoursUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin", "pwd");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void purchase_ok_updates_balance() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(2L).academy(academy).username("pilot").password("x").fullName("Pilot").build();
        final User admin = User.builder().id(3L).academy(academy).username("admin").password("y").fullName("Admin").build();
        final Aircraft aircraft = Aircraft.builder().id(4L).academy(academy).tailNumber("HK-1").model("C172").type("SEL").build();
        final AcademyModule mod = AcademyModule.builder().id(10L).academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(2L);
        req.setAircraftId(4L);
        req.setReceiptNumber("R-1");
        req.setHours(5.5);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(mod));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(pilot));
        when(aircraftRepository.findById(eq(4L))).thenReturn(Optional.of(aircraft));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft(eq("R-1"), eq(aircraft))).thenReturn(false);
        when(userRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(admin));
        when(hourPurchaseRepository.save(any(HourPurchase.class))).thenAnswer(inv -> {
            final HourPurchase p = inv.getArgument(0, HourPurchase.class);
            return HourPurchase.builder()
                    .id(100L)
                    .academy(p.getAcademy())
                    .pilot(p.getPilot())
                    .aircraft(p.getAircraft())
                    .receiptNumber(p.getReceiptNumber())
                    .hours(p.getHours())
                    .purchaseDate(p.getPurchaseDate())
                    .createdBy(p.getCreatedBy())
                    .build();
        });
        when(balanceRepository.findByPilotAndAircraft(eq(pilot), eq(aircraft))).thenReturn(Optional.empty());

        final com.espectrosoft.flightTracker.application.dto.hours.PurchaseHoursResponseDto resp = useCase.purchaseHours(req);

        assertEquals(100L, resp.getPurchaseId());
        assertEquals(5.5, resp.getBalanceHours());
        verify(academyRepository, times(1)).findById(eq(1L));
        verify(academyModuleRepository, times(1)).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(userRepository, times(1)).findById(eq(2L));
        verify(aircraftRepository, times(1)).findById(eq(4L));
        verify(hourPurchaseRepository, times(1)).existsByReceiptNumberAndAircraft(eq("R-1"), eq(aircraft));
        verify(userRepository, times(1)).findByUsername(eq("admin"));
        verify(hourPurchaseRepository, times(1)).save(any(HourPurchase.class));
        verify(balanceRepository, times(1)).save(any(UserAircraftBalance.class));
    }

    @Test
    void purchase_duplicate_receipt_throws() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(2L).academy(academy).username("pilot").password("x").fullName("Pilot").build();
        final User admin = User.builder().id(3L).academy(academy).username("admin").password("y").fullName("Admin").build();
        final Aircraft aircraft = Aircraft.builder().id(4L).academy(academy).tailNumber("HK-1").model("C172").type("SEL").build();
        final AcademyModule mod = AcademyModule.builder().id(10L).academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(2L);
        req.setAircraftId(4L);
        req.setReceiptNumber("R-1");
        req.setHours(2.0);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(mod));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(pilot));
        when(aircraftRepository.findById(eq(4L))).thenReturn(Optional.of(aircraft));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft(eq("R-1"), eq(aircraft))).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.purchaseHours(req));
        verify(academyRepository, times(1)).findById(eq(1L));
        verify(academyModuleRepository, times(1)).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(userRepository, times(1)).findById(eq(2L));
        verify(aircraftRepository, times(1)).findById(eq(4L));
        verify(hourPurchaseRepository, times(1)).existsByReceiptNumberAndAircraft(eq("R-1"), eq(aircraft));
        verify(hourPurchaseRepository, times(0)).save(any(HourPurchase.class));
    }

    @Test
    void usage_ok_updates_balance() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(2L).academy(academy).username("pilot").password("x").fullName("Pilot").build();
        final User instructor = User.builder().id(5L).academy(academy).username("instr").password("z").fullName("Instr").build();
        final User admin = User.builder().id(3L).academy(academy).username("admin").password("y").fullName("Admin").build();
        final Aircraft aircraft = Aircraft.builder().id(4L).academy(academy).tailNumber("HK-1").model("C172").type("SEL").build();
        final AcademyModule mod = AcademyModule.builder().id(10L).academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(2L);
        req.setAircraftId(4L);
        req.setInstructorId(5L);
        req.setHours(1.5);
        req.setFlightDate(LocalDate.now());
        req.setLogbookNumber("L-1");

        final UserAircraftBalance bal = UserAircraftBalance.builder().id(20L).pilot(pilot).aircraft(aircraft).totalPurchased(5.0).totalUsed(0.0).balanceHours(5.0).build();

        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(mod));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(pilot));
        when(userRepository.findById(eq(5L))).thenReturn(Optional.of(instructor));
        when(aircraftRepository.findById(eq(4L))).thenReturn(Optional.of(aircraft));
        when(balanceRepository.findByPilotAndAircraft(eq(pilot), eq(aircraft))).thenReturn(Optional.of(bal));
        when(userRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(admin));
        when(hourUsageRepository.save(any(HourUsage.class))).thenAnswer(inv -> {
            final HourUsage u = inv.getArgument(0, HourUsage.class);
            return HourUsage.builder()
                    .id(200L)
                    .academy(u.getAcademy())
                    .pilot(u.getPilot())
                    .aircraft(u.getAircraft())
                    .instructor(u.getInstructor())
                    .hours(u.getHours())
                    .flightDate(u.getFlightDate())
                    .logbookNumber(u.getLogbookNumber())
                    .createdBy(u.getCreatedBy())
                    .build();
        });

        final com.espectrosoft.flightTracker.application.dto.hours.RegisterUsageResponseDto resp = useCase.registerUsage(req);

        assertEquals(200L, resp.getUsageId());
        assertEquals(3.5, resp.getBalanceHours());
        verify(academyRepository, times(1)).findById(eq(1L));
        verify(academyModuleRepository, times(1)).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(userRepository, times(1)).findById(eq(2L));
        verify(userRepository, times(1)).findById(eq(5L));
        verify(aircraftRepository, times(1)).findById(eq(4L));
        verify(balanceRepository, times(1)).findByPilotAndAircraft(eq(pilot), eq(aircraft));
        verify(userRepository, times(1)).findByUsername(eq("admin"));
        verify(hourUsageRepository, times(1)).save(any(HourUsage.class));
        verify(balanceRepository, times(1)).save(any(UserAircraftBalance.class));
    }

    @Test
    void usage_insufficient_balance_throws() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(2L).academy(academy).username("pilot").password("x").fullName("Pilot").build();
        final User instructor = User.builder().id(5L).academy(academy).username("instr").password("z").fullName("Instr").build();
        final Aircraft aircraft = Aircraft.builder().id(4L).academy(academy).tailNumber("HK-1").model("C172").type("SEL").build();
        final AcademyModule mod = AcademyModule.builder().id(10L).academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        final RegisterUsageRequestDto req = new RegisterUsageRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(2L);
        req.setAircraftId(4L);
        req.setInstructorId(5L);
        req.setHours(10.0);
        req.setFlightDate(LocalDate.now());
        req.setLogbookNumber("L-1");

        final UserAircraftBalance bal = UserAircraftBalance.builder().id(20L).pilot(pilot).aircraft(aircraft).totalPurchased(5.0).totalUsed(0.0).balanceHours(1.0).build();

        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(Optional.of(mod));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(pilot));
        when(userRepository.findById(eq(5L))).thenReturn(Optional.of(instructor));
        when(aircraftRepository.findById(eq(4L))).thenReturn(Optional.of(aircraft));
        when(balanceRepository.findByPilotAndAircraft(eq(pilot), eq(aircraft))).thenReturn(Optional.of(bal));

        assertThrows(BusinessException.class, () -> useCase.registerUsage(req));
        verify(academyRepository, times(1)).findById(eq(1L));
        verify(academyModuleRepository, times(1)).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(userRepository, times(1)).findById(eq(2L));
        verify(userRepository, times(1)).findById(eq(5L));
        verify(aircraftRepository, times(1)).findById(eq(4L));
        verify(balanceRepository, times(1)).findByPilotAndAircraft(eq(pilot), eq(aircraft));
        verify(hourUsageRepository, times(0)).save(any(HourUsage.class));
    }

    @Test
    void purchase_receipt_must_match_aircraft_type() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User pilot = User.builder().id(2L).academy(academy).username("pilot").password("x").fullName("Pilot").build();
        final Aircraft aircraftRequested = Aircraft.builder().id(4L).academy(academy).tailNumber("HK-1").model("C172").type("SEL").build();
        final Aircraft aircraftExisting = Aircraft.builder().id(5L).academy(academy).tailNumber("HK-2").model("C310").type("MEL").build();
        final HourPurchase existingPurchase = HourPurchase.builder()
                .id(50L)
                .academy(academy)
                .pilot(pilot)
                .aircraft(aircraftExisting)
                .receiptNumber("R-2")
                .hours(2.0)
                .purchaseDate(java.time.LocalDate.now())
                .createdBy(pilot)
                .build();
        final AcademyModule mod = AcademyModule.builder().id(10L).academy(academy).moduleCode(ModuleCode.HOURS).active(true).build();
        final PurchaseHoursRequestDto req = new PurchaseHoursRequestDto();
        req.setAcademyId(1L);
        req.setPilotId(2L);
        req.setAircraftId(4L);
        req.setReceiptNumber("R-2");
        req.setHours(1.0);
        req.setPurchaseDate(LocalDate.now());

        when(academyRepository.findById(eq(1L))).thenReturn(java.util.Optional.of(academy));
        when(academyModuleRepository.findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS))).thenReturn(java.util.Optional.of(mod));
        when(userRepository.findById(eq(2L))).thenReturn(java.util.Optional.of(pilot));
        when(aircraftRepository.findById(eq(4L))).thenReturn(java.util.Optional.of(aircraftRequested));
        when(hourPurchaseRepository.existsByReceiptNumberAndAircraft(eq("R-2"), eq(aircraftRequested))).thenReturn(false);
        when(hourPurchaseRepository.findByAcademyAndReceiptNumber(eq(academy), eq("R-2")))
                .thenReturn(java.util.List.of(existingPurchase));

        assertThrows(BusinessException.class, () -> useCase.purchaseHours(req));
        verify(academyRepository, times(1)).findById(eq(1L));
        verify(academyModuleRepository, times(1)).findByAcademyAndModuleCode(eq(academy), eq(ModuleCode.HOURS));
        verify(userRepository, times(1)).findById(eq(2L));
        verify(aircraftRepository, times(1)).findById(eq(4L));
        verify(hourPurchaseRepository, times(1)).existsByReceiptNumberAndAircraft(eq("R-2"), eq(aircraftRequested));
        verify(hourPurchaseRepository, times(1)).findByAcademyAndReceiptNumber(eq(academy), eq("R-2"));
        verify(hourPurchaseRepository, times(0)).save(any(HourPurchase.class));
    }
}
