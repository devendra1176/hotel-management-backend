package com.project.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsDTO {

    private long totalUsers;
    private long totalRooms;
    private long totalBookings;
    private long availableRooms;
}