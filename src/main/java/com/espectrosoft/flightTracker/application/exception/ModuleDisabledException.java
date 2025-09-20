package com.espectrosoft.flightTracker.application.exception;

public class ModuleDisabledException extends RuntimeException {
    public ModuleDisabledException(String message) {
        super(message);
    }
}
