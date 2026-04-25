package com.project.hotel.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomResponseDTO {

    private Long id;
    private String roomNumber;
    private String type;
    private double price;
    private boolean available;
}