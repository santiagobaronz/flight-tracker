package com.espectrosoft.flightTracker.application.exception.types;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
}
