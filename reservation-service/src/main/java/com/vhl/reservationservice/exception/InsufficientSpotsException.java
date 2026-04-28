package com.vhl.reservationservice.exception;

public class InsufficientSpotsException extends RuntimeException {
    public InsufficientSpotsException(String message) {
        super(message);
    }

    public InsufficientSpotsException(String message, Throwable cause) {
        super(message, cause);
    }
}
