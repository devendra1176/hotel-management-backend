package com.project.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelManagementOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Management System API")
                        .version("1.0")
                        .description("Spring Boot Hotel Management Backend with JWT Security, Role-Based Authorization, Room Management, Booking System, Validation, Logging, and Pagination.")
                        .contact(new Contact()
                                .name("Devendra")
                                .email("devendra1176sahu@gmail.com")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}