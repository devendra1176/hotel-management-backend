package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.RoomRequestDTO;
import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Room APIs", description = "Room creation, listing, filtering, searching, and availability APIs")
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(
            summary = "Create a new room",
            description = "ADMIN only API. Creates a new hotel room with room number, room type, price, and default availability."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO dto) {

        RoomResponseDTO room = roomService.createRoom(dto);

        return new ApiResponse<>(201, "Room created successfully", room);
    }

    @Operation(
            summary = "Search rooms by type and max price",
            description = "Fetches available rooms using room type, maximum price, page number, and page size."
    )
    @GetMapping("/search")
    public ApiResponse<Page<RoomResponseDTO>> searchRooms(
            @Parameter(description = "Room type", example = "DELUXE")
            @RequestParam String type,

            @Parameter(description = "Maximum room price", example = "3000")
            @RequestParam double maxPrice,

            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam int size) {

        Page<RoomResponseDTO> rooms =
                roomService.searchRooms(type, maxPrice, page, size);

        return new ApiResponse<>(200, "Rooms fetched successfully", rooms);
    }

    @Operation(
            summary = "Get all rooms",
            description = "Fetches paginated and sorted room list. Requires USER or ADMIN role."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ApiResponse<Page<RoomResponseDTO>> getRooms(
            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam int size,

            @Parameter(description = "Field name used for sorting", example = "price")
            @RequestParam(defaultValue = "price") String sortBy) {

        Page<RoomResponseDTO> rooms =
                roomService.getAllRooms(page, size, sortBy);

        return new ApiResponse<>(200, "Rooms fetched successfully", rooms);
    }

    @Operation(
            summary = "Delete room",
            description = "ADMIN only API. Deletes a room by room id."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoom(
            @Parameter(description = "Room id", example = "1")
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return new ApiResponse<>(200, "Room deleted successfully", null);
    }

    @Operation(
            summary = "Dynamic room search",
            description = "Searches rooms using optional filters: room type, maximum price, and availability."
    )
    @GetMapping("/search-dynamic")
    public ApiResponse<List<RoomResponseDTO>> searchRoomsDynamic(
            @Parameter(description = "Room type", example = "DELUXE")
            @RequestParam(required = false) String type,

            @Parameter(description = "Maximum room price", example = "3000")
            @RequestParam(required = false) Double maxPrice,

            @Parameter(description = "Room availability", example = "true")
            @RequestParam(required = false) Boolean available) {

        List<RoomResponseDTO> rooms =
                roomService.searchRoomsDynamic(type, maxPrice, available);

        String message = rooms.isEmpty()
                ? "No rooms found matching criteria"
                : "Filtered rooms fetched successfully";

        return new ApiResponse<>(200, message, rooms);
    }

    @Operation(
            summary = "Advanced room search",
            description = "Searches available rooms by check-in date, check-out date, optional room type, optional max price, pagination, and sorting."
    )
    @GetMapping("/search-advanced")
    public ApiResponse<Page<RoomResponseDTO>> searchAdvanced(
            @Parameter(description = "Check-in date in yyyy-MM-dd format", example = "2026-05-01")
            @RequestParam String checkIn,

            @Parameter(description = "Check-out date in yyyy-MM-dd format", example = "2026-05-05")
            @RequestParam String checkOut,

            @Parameter(description = "Room type", example = "DELUXE")
            @RequestParam(required = false) String type,

            @Parameter(description = "Maximum room price", example = "3000")
            @RequestParam(required = false) Double maxPrice,

            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam(defaultValue = "5") int size,

            @Parameter(description = "Field name used for sorting", example = "price")
            @RequestParam(defaultValue = "price") String sortBy) {

        Page<RoomResponseDTO> rooms =
                roomService.searchAdvanced(checkIn, checkOut, type, maxPrice, page, size, sortBy);

        return new ApiResponse<>(200, "Filtered rooms fetched successfully", rooms);
    }

    @Operation(
            summary = "Get available rooms by date",
            description = "Returns rooms that are available for the given check-in and check-out date range."
    )
    @GetMapping("/available")
    public ApiResponse<Page<RoomResponseDTO>> getAvailableRoomsByDate(
            @Parameter(description = "Check-in date in yyyy-MM-dd format", example = "2026-05-01")
            @RequestParam String checkIn,

            @Parameter(description = "Check-out date in yyyy-MM-dd format", example = "2026-05-05")
            @RequestParam String checkOut,

            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam(defaultValue = "5") int size,

            @Parameter(description = "Field name used for sorting", example = "price")
            @RequestParam(defaultValue = "price") String sortBy) {

        Page<RoomResponseDTO> rooms = roomService.getAvailableRoomsByDate(
                checkIn,
                checkOut,
                page,
                size,
                sortBy
        );

        return new ApiResponse<>(200, "Available rooms fetched successfully", rooms);
    }
}