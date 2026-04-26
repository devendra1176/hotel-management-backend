package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response DTO containing booking details")
public class BookingResponseDTO {

    @Schema(
            description = "Unique booking id",
            example = "10"
    )
    private Long bookingId;

    @Schema(
            description = "Name of the user who created the booking",
            example = "Devendra Sahu"
    )
    private String userName;

    @Schema(
            description = "Room number associated with the booking",
            example = "101"
    )
    private String roomNumber;

    @Schema(
            description = "Check-in date (yyyy-MM-dd)",
            example = "2026-05-01"
    )
    private String checkIn;

    @Schema(
            description = "Check-out date (yyyy-MM-dd)",
            example = "2026-05-05"
    )
    private String checkOut;
}