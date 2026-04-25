package com.project.hotel.exception;

public class UnauthorizedBookingAccessException extends RuntimeException {

    public UnauthorizedBookingAccessException(String message) {
        super(message);
    }
}