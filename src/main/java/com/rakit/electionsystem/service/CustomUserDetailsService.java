package com.rakit.electionsystem.service;

import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user details from the database for authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final VoterRepository voterRepository;

    /**
     * Loads a user by their username (email).
     *
     * @param username the username (email)
     * @return UserDetails object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        Voter voter = voterRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with email: %s", username)
                ));

        log.debug("User found: {}", voter.getEmail());
        return voter;
    }

    /**
     * Loads a user by their ID.
     *
     * @param id the user ID
     * @return UserDetails object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", id);

        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with id: %s", id)
                ));

        log.debug("User found: {}", voter.getEmail());
        return voter;
    }
}
