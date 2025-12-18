package com.rakit.electionsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the Election System API.
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Election System API",
        version = "1.0.0",
        description = "RESTful API for secure, anonymous voting system with JWT authentication and role-based access control"
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Development server")
    }
)
@SecurityScheme(
    name = "JWT Bearer Token",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter JWT token obtained from /api/auth/login"
)
public class OpenApiConfig {
}
