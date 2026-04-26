package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Request DTO for creating a new hotel room")
public class RoomRequestDTO {

    @Schema(
            description = "Unique room number",
            example = "101"
    )
    @NotBlank(message = "Room number required")
    private String roomNumber;

    @Schema(
            description = "Room type. Allowed values: STANDARD, DELUXE, SUITE",
            example = "SUITE / DELUXE / STANDARD"
    )
    @NotBlank(message = "Room type is required")
    private String type;

    @Schema(
            description = "Room price per booking/night",
            example = "2500"
    )
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Min(value = 100, message = "Price must be greater than 100 Rs")
    private Double price;
}