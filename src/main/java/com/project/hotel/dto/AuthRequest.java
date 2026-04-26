package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request DTO for user login")
public class AuthRequest {

    @Schema(
            description = "Registered user email",
            example = "admin@mail.com"
    )
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "User password",
            example = "admin123"
    )
    @NotBlank(message = "Password is required")
    private String password;
}