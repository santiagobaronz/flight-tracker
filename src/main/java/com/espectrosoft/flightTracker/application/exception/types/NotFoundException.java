package com.espectrosoft.flightTracker.application.exception.types;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
