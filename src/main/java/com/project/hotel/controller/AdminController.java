package com.project.hotel.controller;

import com.project.hotel.dto.AdminStatsDTO;
import com.project.hotel.dto.ApiResponse;
import com.project.hotel.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin APIs", description = "Admin dashboard and system statistics APIs")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Get admin dashboard stats",
            description = "ADMIN only API. Returns system statistics such as total users, total rooms, total bookings, and available rooms."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ApiResponse<AdminStatsDTO> getAdminStats() {

        AdminStatsDTO stats = adminService.getAdminStats();

        return new ApiResponse<>(
                200,
                "Admin stats fetched successfully",
                stats
        );
    }
}