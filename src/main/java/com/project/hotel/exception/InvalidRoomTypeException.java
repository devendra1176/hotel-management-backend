package com.project.hotel.exception;

public class InvalidRoomTypeException extends RuntimeException {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}