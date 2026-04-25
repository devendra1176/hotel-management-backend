package com.project.hotel.exception;

public class RoomAlreadyBookedException extends RuntimeException {

    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}