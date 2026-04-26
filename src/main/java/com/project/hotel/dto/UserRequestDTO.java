package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating a new user account")
public class UserRequestDTO {

    @Schema(
            description = "Full name of the user",
            example = "Devendra Sahu"
    )
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Schema(
            description = "User email address (must be unique)",
            example = "devendra@mail.com"
    )
    @Email(message = "Invalid email")
    @NotBlank(message = "Email required")
    private String email;

    @Schema(
            description = "User password (minimum 6 characters)",
            example = "Password@123"
    )
    @Size(min = 6, message = "Password must be at least 6 character")
    private String password;

    // getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}