package com.rakit.electionsystem.dto;

import com.rakit.electionsystem.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for voter information returned to admin users.
 * Excludes sensitive information like passwords.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoterResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String pesel;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
