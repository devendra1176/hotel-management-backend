package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth APIs", description = "Authentication and JWT token generation APIs")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates a user using email and password. If credentials are valid, returns a JWT token containing user email and role."
    )
    @PostMapping("/login")
    public ApiResponse<String> login(
            @Parameter(description = "Registered user email", example = "admin@gmail.com")
            @RequestParam String email,

            @Parameter(description = "User password", example = "password123")
            @RequestParam String password) {

        log.info("Login attempt started for email={}", email);

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            log.info("Login successful for email={}, role={}", email, user.getRole().name());

            return new ApiResponse<>(200, "Login successful", token);

        } catch (BadCredentialsException ex) {
            log.warn("Login failed due to bad credentials for email={}", email);
            throw ex;
        }
    }
}