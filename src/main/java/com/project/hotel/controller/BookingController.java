package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.BookingRequestDTO;
import com.project.hotel.dto.BookingResponseDTO;
import com.project.hotel.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking APIs", description = "Booking creation, cancellation, ownership control, and booking history APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Create booking",
            description = "USER or ADMIN can create a booking. The logged-in user is identified from the JWT token, so userId is not required in the request body."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ApiResponse<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingRequestDTO dto,
            Authentication authentication) {

        String email = authentication.getName();

        BookingResponseDTO res = bookingService.createBooking(dto, email);

        return new ApiResponse<>(201, "Booking created successfully", res);
    }

    @Operation(
            summary = "Cancel booking",
            description = "USER can cancel only their own booking. ADMIN can cancel any user's booking."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> cancelBooking(
            @Parameter(description = "Booking id", example = "1")
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        bookingService.cancelBooking(id, email);

        return new ApiResponse<>(200, "Booking cancelled successfully", null);
    }

    @Operation(
            summary = "Get my booking history",
            description = "Fetches the logged-in user's own booking history with pagination and sorting."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/my")
    public ApiResponse<Page<BookingResponseDTO>> getMyBookingHistory(
            Authentication authentication,

            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam(defaultValue = "5") int size,

            @Parameter(description = "Field name used for sorting", example = "id")
            @RequestParam(defaultValue = "id") String sortBy) {

        String email = authentication.getName();

        Page<BookingResponseDTO> bookings =
                bookingService.getMyBookingHistory(email, page, size, sortBy);

        return new ApiResponse<>(200, "My booking history fetched successfully", bookings);
    }

    @Operation(
            summary = "Get all booking history",
            description = "ADMIN only API. Fetches all users' booking history with pagination and sorting."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<BookingResponseDTO>> getAllBookingHistory(
            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "5")
            @RequestParam(defaultValue = "5") int size,

            @Parameter(description = "Field name used for sorting", example = "id")
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<BookingResponseDTO> bookings =
                bookingService.getAllBookingHistory(page, size, sortBy);

        return new ApiResponse<>(200, "All booking history fetched successfully", bookings);
    }
}