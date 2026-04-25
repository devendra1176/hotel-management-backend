package com.project.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponseDTO {

    private Long bookingId;
    private String userName;
    private String roomNumber;

    private String checkIn;
    private String checkOut;

}
