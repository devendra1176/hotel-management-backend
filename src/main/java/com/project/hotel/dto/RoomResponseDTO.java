package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Response DTO containing room details")
public class RoomResponseDTO {

    @Schema(
            description = "Unique room id",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Room number",
            example = "101"
    )
    private String roomNumber;

    @Schema(
            description = "Room type",
            example = "DELUXE"
    )
    private String type;

    @Schema(
            description = "Room price per night",
            example = "2500"
    )
    private double price;

    @Schema(
            description = "Availability status of the room",
            example = "true"
    )
    private boolean available;
}