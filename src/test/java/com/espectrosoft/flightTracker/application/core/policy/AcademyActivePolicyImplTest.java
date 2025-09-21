package com.espectrosoft.flightTracker.application.core.policy;

import com.espectrosoft.flightTracker.application.core.policy.validations.AcademyActivePolicyImpl;
import com.espectrosoft.flightTracker.application.exception.AcademyInactiveException;
import com.espectrosoft.flightTracker.domain.model.Academy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcademyActivePolicyImplTest {

    @Test
    void active_ok() {
        final Academy academy = Academy.builder().id(1L).name("A").address("Addr").phoneNumber("123").build();
        final AcademyActivePolicyImpl
            policy = new AcademyActivePolicyImpl();
        assertDoesNotThrow(() -> policy.apply(academy));
    }

    @Test
    void inactive_throws() {
        final Academy academy = Academy.builder().id(1L).name("A").address("Addr").phoneNumber("123").active(false).build();
        final AcademyActivePolicyImpl policy = new AcademyActivePolicyImpl();
        assertThrows(AcademyInactiveException.class, () -> policy.apply(academy));
    }

    @Test
    void null_throws() {
        final AcademyActivePolicyImpl policy = new AcademyActivePolicyImpl();
        assertThrows(AcademyInactiveException.class, () -> policy.apply(null));
    }
}
