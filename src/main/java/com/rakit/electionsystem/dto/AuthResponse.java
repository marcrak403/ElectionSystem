package com.rakit.electionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @Builder.Default
    private String type = "Bearer";
    private String token;
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public AuthResponse(String token, Long id, String email, String firstName, String lastName, String role) {
        this.type = "Bearer";
        this.token = token;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
