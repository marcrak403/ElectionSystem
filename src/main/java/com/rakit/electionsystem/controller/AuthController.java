package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.AuthResponse;
import com.rakit.electionsystem.dto.ErrorResponse;
import com.rakit.electionsystem.dto.LoginRequest;
import com.rakit.electionsystem.dto.RegisterRequest;
import com.rakit.electionsystem.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     *
     * @param request the registration request
     * @return authentication response with JWT token
     */
    @Operation(summary = "Register a new user",
               description = "Creates a new voter account and returns a JWT token for immediate authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or duplicate email",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - Registering new user: {}", request.getEmail());
        AuthResponse response = authenticationService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login request
     * @return authentication response with JWT token
     */
    @Operation(summary = "Login",
               description = "Authenticates a user with email and password, returning a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                     content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login attempt for user: {}", request.getEmail());
        AuthResponse response = authenticationService.login(request);
        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
}
