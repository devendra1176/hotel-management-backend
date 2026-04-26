package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO containing user details returned by the API")
public class UserResponseDTO {

    @Schema(
            description = "Unique user id",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Full name of the user",
            example = "Devendra Sahu"
    )
    private String name;

    @Schema(
            description = "User email address",
            example = "devendra@gmail.com"
    )
    private String email;

    @Schema(
            description = "User role in the system",
            example = "USER"
    )
    private String role;

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}