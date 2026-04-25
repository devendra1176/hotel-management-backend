package com.project.hotel.controller;

import com.project.hotel.dto.AdminStatsDTO;
import com.project.hotel.dto.ApiResponse;
import com.project.hotel.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
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