package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.BookingRequestDTO;
import com.project.hotel.dto.BookingResponseDTO;
import com.project.hotel.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking APIs", description = "Booking creation, cancellation, ownership control, and booking history APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(
            summary = "Create booking",
            description = "USER or ADMIN can create booking. Logged-in user is identified from JWT token."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ApiResponse<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO dto,
            Authentication authentication) {

        String email = authentication.getName();

        BookingResponseDTO res = bookingService.createBooking(dto, email);

        return new ApiResponse<>(201, "Booking successful", res);
    }

    @Operation(
            summary = "Cancel booking",
            description = "USER can cancel own booking. ADMIN can cancel any booking."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        bookingService.cancelBooking(id, email);

        return new ApiResponse<>(200, "Booking cancelled successfully", null);
    }

    @Operation(
            summary = "My booking history",
            description = "Fetch logged-in user's own booking history with pagination"
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/my")
    public ApiResponse<Page<BookingResponseDTO>> getMyBookingHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        String email = authentication.getName();

        Page<BookingResponseDTO> bookings =
                bookingService.getMyBookingHistory(email, page, size, sortBy);

        return new ApiResponse<>(200, "My booking history fetched", bookings);
    }

    @Operation(
            summary = "All booking history",
            description = "ADMIN only API to fetch all users' booking history with pagination"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<BookingResponseDTO>> getAllBookingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<BookingResponseDTO> bookings =
                bookingService.getAllBookingHistory(page, size, sortBy);

        return new ApiResponse<>(200, "All booking history fetched", bookings);
    }
}