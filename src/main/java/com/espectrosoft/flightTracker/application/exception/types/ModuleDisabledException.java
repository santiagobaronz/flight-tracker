package com.espectrosoft.flightTracker.application.exception.types;

public class ModuleDisabledException extends RuntimeException {
    public ModuleDisabledException(String message) {
        super(message);
    }
}
