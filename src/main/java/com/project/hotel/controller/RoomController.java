package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.RoomRequestDTO;
import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Room APIs", description = "Room creation, listing, search, filtering, and availability APIs")
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(
            summary = "Create room",
            description = "ADMIN only API to create a new hotel room"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO dto) {

        RoomResponseDTO room = roomService.createRoom(dto);

        return new ApiResponse<>(201, "Room created", room);
    }

    @Operation(
            summary = "Search rooms by type and max price",
            description = "Fetch available rooms using room type, maximum price, pagination, and size"
    )
    @GetMapping("/search")
    public ApiResponse<Page<RoomResponseDTO>> searchRooms(
            @RequestParam String type,
            @RequestParam double maxPrice,
            @RequestParam int page,
            @RequestParam int size) {

        Page<RoomResponseDTO> rooms =
                roomService.searchRooms(type, maxPrice, page, size);

        return new ApiResponse<>(200, "Rooms fetched", rooms);
    }

    @Operation(
            summary = "Get all rooms",
            description = "Fetch paginated and sorted room list. USER and ADMIN can access this API."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ApiResponse<Page<RoomResponseDTO>> getRooms(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "price") String sortBy) {

        Page<RoomResponseDTO> rooms =
                roomService.getAllRooms(page, size, sortBy);

        return new ApiResponse<>(200, "Rooms fetched", rooms);
    }

    @Operation(
            summary = "Delete room",
            description = "ADMIN only API to delete a room by room id"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoom(@PathVariable Long id) {

        roomService.deleteRoom(id);

        return new ApiResponse<>(200, "Room deleted", null);
    }

    @Operation(
            summary = "Dynamic room search",
            description = "Search rooms using optional filters: type, maxPrice, and availability"
    )
    @GetMapping("/search-dynamic")
    public ApiResponse<List<RoomResponseDTO>> searchRoomsDynamic(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxPrice,
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
            description = "Search available rooms by check-in date, check-out date, optional room type, optional max price, pagination, and sorting"
    )
    @GetMapping("/search-advanced")
    public ApiResponse<Page<RoomResponseDTO>> searchAdvanced(
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "price") String sortBy
    ) {

        Page<RoomResponseDTO> rooms =
                roomService.searchAdvanced(checkIn, checkOut, type, maxPrice, page, size, sortBy);

        return new ApiResponse<>(200, "Filtered rooms fetched", rooms);
    }

    @Operation(
            summary = "Get available rooms by date",
            description = "Returns rooms that are available for the given check-in and check-out date range"
    )
    @GetMapping("/available")
    public ApiResponse<Page<RoomResponseDTO>> getAvailableRoomsByDate(
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
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