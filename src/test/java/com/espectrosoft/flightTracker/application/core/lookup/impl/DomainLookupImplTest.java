package com.espectrosoft.flightTracker.application.core.lookup.impl;

import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.User;
import com.espectrosoft.flightTracker.domain.model.enums.AircraftType;
import com.espectrosoft.flightTracker.domain.repository.AcademyRepository;
import com.espectrosoft.flightTracker.domain.repository.AircraftRepository;
import com.espectrosoft.flightTracker.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainLookupImplTest {

    @Mock
    private AcademyRepository academyRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private UserRepository userRepository;

    private DomainLookupImpl lookup;

    @BeforeEach
    void setUp() {
        lookup = new DomainLookupImpl(academyRepository, aircraftRepository, userRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireAcademy_found() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        when(academyRepository.findById(eq(1L))).thenReturn(Optional.of(academy));

        final Academy res = lookup.requireAcademy(1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        verify(academyRepository).findById(eq(1L));
    }

    @Test
    void requireAcademy_not_found_throws() {
        when(academyRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> lookup.requireAcademy(1L));
        verify(academyRepository).findById(eq(1L));
    }

    @Test
    void requireAircraft_found() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final Aircraft aircraft = Aircraft.builder().id(100L).academy(academy).registration("HK").model("M").type(AircraftType.AIRCRAFT).build();
        when(aircraftRepository.findById(eq(100L))).thenReturn(Optional.of(aircraft));

        final Aircraft res = lookup.requireAircraft(100L);

        assertNotNull(res);
        assertEquals(100L, res.getId());
        verify(aircraftRepository).findById(eq(100L));
    }

    @Test
    void requireAircraft_not_found_throws() {
        when(aircraftRepository.findById(eq(100L))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> lookup.requireAircraft(100L));
        verify(aircraftRepository).findById(eq(100L));
    }

    @Test
    void requireCurrentUser_found() {
        final Academy academy = Academy.builder().id(1L).name("A").build();
        final User user = User.builder().id(2L).username("admin").academy(academy).fullName("Admin").password("p").build();
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("admin", ""));
        SecurityContextHolder.setContext(context);
        when(userRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(user));

        final User res = lookup.requireCurrentUser();

        assertNotNull(res);
        assertEquals("admin", res.getUsername());
        verify(userRepository).findByUsername(eq("admin"));
    }

    @Test
    void requireCurrentUser_no_auth_throws() {
        SecurityContextHolder.clearContext();
        assertThrows(NotFoundException.class, () -> lookup.requireCurrentUser());
    }

    @Test
    void requireCurrentUser_not_found_throws() {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("ghost", ""));
        SecurityContextHolder.setContext(context);
        when(userRepository.findByUsername(eq("ghost"))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> lookup.requireCurrentUser());
        verify(userRepository).findByUsername(eq("ghost"));
    }
}
