package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.UserRequestDTO;
import com.project.hotel.dto.UserResponseDTO;
import com.project.hotel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User APIs", description = "User management APIs (signup, fetch, delete)")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register new user",
            description = "Public API to create a new user account. Role is automatically assigned as USER."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO dto) {

        UserResponseDTO user = userService.createUser(dto);

        return new ApiResponse<>(201, "User created successfully", user);
    }

    @Operation(
            summary = "Get all users",
            description = "ADMIN only API. Fetches paginated and sorted list of all users."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<UserResponseDTO>> getAllUsers(

            @Parameter(description = "Page number starts from 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field name used for sorting", example = "id")
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<UserResponseDTO> users =
                userService.getAllUsers(page, size, sortBy);

        return new ApiResponse<>(200, "Users fetched successfully", users);
    }

    @Operation(
            summary = "Get user by id",
            description = "ADMIN only API. Fetches user details by user id."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<UserResponseDTO> getUserById(

            @Parameter(description = "User id", example = "1")
            @PathVariable Long id) {

        UserResponseDTO user = userService.getUserById(id);

        return new ApiResponse<>(200, "User fetched successfully", user);
    }

    @Operation(
            summary = "Delete user",
            description = "ADMIN only API. Deletes a user by user id."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(

            @Parameter(description = "User id", example = "1")
            @PathVariable Long id) {

        userService.deleteUser(id);

        return new ApiResponse<>(200, "User deleted successfully", null);
    }
}