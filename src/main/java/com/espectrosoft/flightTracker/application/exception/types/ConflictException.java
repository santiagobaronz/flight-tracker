package com.espectrosoft.flightTracker.application.exception.types;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
