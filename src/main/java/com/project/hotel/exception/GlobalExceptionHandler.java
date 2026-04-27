package com.project.hotel.exception;

import com.project.hotel.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.security.access.AccessDeniedException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleRoomNotFound(RoomNotFoundException ex) {
        log.warn("RoomNotFoundException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RoomAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleRoomAlreadyExists(RoomAlreadyExistsException ex) {
        log.warn("RoomAlreadyExistsException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ResponseEntity<ApiResponse<String>> handleRoomAlreadyBooked(RoomAlreadyBookedException ex) {
        log.warn("RoomAlreadyBookedException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidRoomTypeException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRoomType(InvalidRoomTypeException ex) {
        log.warn("InvalidRoomTypeException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleBookingNotFound(BookingNotFoundException ex) {
        log.warn("BookingNotFoundException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedBookingAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedBookingAccess(
            UnauthorizedBookingAccessException ex) {
        log.warn("UnauthorizedBookingAccessException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidDateRange(InvalidDateRangeException ex) {
        log.warn("InvalidDateRangeException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidJson(HttpMessageNotReadableException ex) {
        log.warn("Invalid request body or unreadable JSON: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or date format. Please use yyyy-MM-dd"
        );
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidDateFormat(DateTimeParseException ex) {
        log.warn("Invalid date format: parsedString={}", ex.getParsedString());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid date format. Please use yyyy-MM-dd"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation failed: {}", errors);

        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed",
                        errors
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationDenied(
            AuthorizationDeniedException ex) {

        log.warn("Authorization denied: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied. You do not have permission to use this API"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(
            AccessDeniedException ex) {

        log.warn("Access denied: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied. You do not have permission to use this API"
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Login failed: bad credentials");
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("Login failed: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid request parameter: name={}, value={}", ex.getName(), ex.getValue());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: " + ex.getName()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {

        log.error("Unexpected error occurred", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong"
        );
    }

    private ResponseEntity<ApiResponse<String>> buildErrorResponse(
            HttpStatus status,
            String message) {

        ApiResponse<String> response =
                new ApiResponse<>(
                        status.value(),
                        message,
                        null
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}