package com.project.hotel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(
            description = "HTTP status code of the response",
            example = "200"
    )
    private final int status;

    @Schema(
            description = "Human-readable message describing the result",
            example = "Request processed successfully"
    )
    private final String message;

    @Schema(
            description = "Actual response data (can be object, list, or null)"
    )
    private final T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}