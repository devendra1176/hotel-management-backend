package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Response DTO containing admin dashboard statistics")
public class AdminStatsDTO {

    @Schema(
            description = "Total number of registered users in the system",
            example = "25"
    )
    private long totalUsers;

    @Schema(
            description = "Total number of rooms in the hotel",
            example = "50"
    )
    private long totalRooms;

    @Schema(
            description = "Total number of bookings made",
            example = "120"
    )
    private long totalBookings;

    @Schema(
            description = "Number of rooms currently available for booking",
            example = "18"
    )
    private long availableRooms;
}