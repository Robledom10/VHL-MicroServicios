package com.vhl.reservationservice.exception;

public class ReservationException extends RuntimeException {
    
    private String errorCode;

    public ReservationException(String message) {
        super(message);
        this.errorCode = "RESERVATION_ERROR";
    }

    public ReservationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "RESERVATION_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
