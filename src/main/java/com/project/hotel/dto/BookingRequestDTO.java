package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request DTO for creating a new booking")
public class BookingRequestDTO {

    @Schema(
            description = "Room id for which booking is requested",
            example = "1"
    )
    @NotNull(message = "Room id is required")
    private Long roomId;

    @Schema(
            description = "Check-in date in yyyy-MM-dd format",
            example = "2026-05-01"
    )
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkIn;

    @Schema(
            description = "Check-out date in yyyy-MM-dd format (must be after check-in)",
            example = "2026-05-05"
    )
    @NotNull(message = "Check-out date is required")
    @FutureOrPresent(message = "Check-out date cannot be in the past")
    private LocalDate checkOut;
}