package com.project.hotel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.ApiResponse;
import com.project.hotel.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ObjectMapper objectMapper;

    // Constructor Injection (clean + testable)
    public SecurityConfig(JwtFilter jwtFilter, ObjectMapper objectMapper) {
        this.jwtFilter = jwtFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF (because JWT is stateless)
                .csrf(csrf -> csrf.disable())

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // Swagger (public)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Auth APIs (login/signup)
                        .requestMatchers("/auth/**", "/admin/login").permitAll()

                        // Public room APIs (view/search)
                        .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()

                        // बाकी सभी APIs → authenticated
                        .anyRequest().authenticated()
                )

                // Exception Handling (VERY IMPORTANT)
                .exceptionHandling(ex -> ex

                        // No token / invalid token → 401
                        .authenticationEntryPoint((request, response, authException) -> {

                            ApiResponse<String> apiResponse =
                                    new ApiResponse<>(
                                            HttpStatus.UNAUTHORIZED.value(),
                                            "Authentication required. Please provide a valid JWT token",
                                            null
                                    );

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");

                            objectMapper.writeValue(response.getWriter(), apiResponse);
                        })

                        // Role not allowed → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            ApiResponse<String> apiResponse =
                                    new ApiResponse<>(
                                            HttpStatus.FORBIDDEN.value(),
                                            "Access denied. You do not have permission to use this API",
                                            null
                                    );

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");

                            objectMapper.writeValue(response.getWriter(), apiResponse);
                        })
                )

                // JWT Filter before Spring Security filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encryption
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager (for login)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}