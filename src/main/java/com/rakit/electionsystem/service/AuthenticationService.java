package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.AuthResponse;
import com.rakit.electionsystem.dto.LoginRequest;
import com.rakit.electionsystem.dto.RegisterRequest;
import com.rakit.electionsystem.exception.DuplicateResourceException;
import com.rakit.electionsystem.exception.InvalidCredentialsException;
import com.rakit.electionsystem.model.Role;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.repository.VoterRepository;
import com.rakit.electionsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling authentication operations (login, register).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new voter.
     *
     * @param request the registration request
     * @return authentication response with JWT token
     * @throws DuplicateResourceException if email or PESEL already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if email already exists
        if (voterRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email already exists: {}", request.getEmail());
            throw new DuplicateResourceException("Voter", "email", request.getEmail());
        }

        // Check if PESEL already exists (if provided)
        if (request.getPesel() != null && voterRepository.existsByPesel(request.getPesel())) {
            log.warn("Registration failed: PESEL already exists: {}", request.getPesel());
            throw new DuplicateResourceException("Voter", "PESEL", request.getPesel());
        }

        // Create new voter
        Voter voter = Voter.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .pesel(request.getPesel())
                .role(Role.ROLE_VOTER)
                .active(true)
                .build();

        Voter savedVoter = voterRepository.save(voter);
        log.info("User registered successfully: {}", savedVoter.getEmail());

        // Generate JWT token
        String token = jwtTokenProvider.generateTokenFromUsername(savedVoter.getEmail());

        return new AuthResponse(
                token,
                savedVoter.getId(),
                savedVoter.getEmail(),
                savedVoter.getFirstName(),
                savedVoter.getLastName(),
                savedVoter.getRole().name()
        );
    }

    /**
     * Authenticates a voter and generates a JWT token.
     *
     * @param request the login request
     * @return authentication response with JWT token
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Voter voter = (Voter) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(authentication);

            log.info("User authenticated successfully: {}", voter.getEmail());

            return new AuthResponse(
                    token,
                    voter.getId(),
                    voter.getEmail(),
                    voter.getFirstName(),
                    voter.getLastName(),
                    voter.getRole().name()
            );
        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for user: {}", request.getEmail());
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Gets the currently authenticated voter.
     *
     * @return the currently authenticated voter
     * @throws InvalidCredentialsException if no user is authenticated
     */
    public Voter getCurrentVoter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new InvalidCredentialsException("No authenticated user found");
        }

        return (Voter) authentication.getPrincipal();
    }
}
