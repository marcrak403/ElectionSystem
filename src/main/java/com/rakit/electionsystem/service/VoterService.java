package com.rakit.electionsystem.service;

import com.rakit.electionsystem.exception.DuplicateResourceException;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.repository.VoterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing voters.
 * Provides CRUD operations and voter-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VoterService {

    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves all voters.
     *
     * @return list of all voters
     */
    @Transactional(readOnly = true)
    public List<Voter> getAllVoters() {
        log.debug("Fetching all voters");
        return voterRepository.findAll();
    }

    /**
     * Retrieves all voters with pagination.
     *
     * @param pageable pagination information
     * @return page of voters
     */
    @Transactional(readOnly = true)
    public Page<Voter> getAllVoters(Pageable pageable) {
        log.debug("Fetching voters with pagination: {}", pageable);
        return voterRepository.findAll(pageable);
    }

    /**
     * Retrieves a voter by ID.
     *
     * @param id the voter ID
     * @return the voter
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional(readOnly = true)
    public Voter getVoterById(Long id) {
        log.debug("Fetching voter by ID: {}", id);
        return voterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", "id", id));
    }

    /**
     * Retrieves a voter by email.
     *
     * @param email the voter email
     * @return the voter
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional(readOnly = true)
    public Voter getVoterByEmail(String email) {
        log.debug("Fetching voter by email: {}", email);
        return voterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Voter", "email", email));
    }

    /**
     * Creates a new voter.
     *
     * @param voter the voter to create
     * @return the created voter
     * @throws DuplicateResourceException if email or PESEL already exists
     */
    @Transactional
    public Voter createVoter(Voter voter) {
        log.info("Creating new voter: {}", voter.getEmail());

        // Check if email already exists
        if (voterRepository.existsByEmail(voter.getEmail())) {
            log.warn("Cannot create voter: email already exists: {}", voter.getEmail());
            throw new DuplicateResourceException("Voter", "email", voter.getEmail());
        }

        // Check if PESEL already exists (if provided)
        if (voter.getPesel() != null && voterRepository.existsByPesel(voter.getPesel())) {
            log.warn("Cannot create voter: PESEL already exists: {}", voter.getPesel());
            throw new DuplicateResourceException("Voter", "PESEL", voter.getPesel());
        }

        // Encode password if it's not already encoded
        if (voter.getPassword() != null && !voter.getPassword().startsWith("$2a$")) {
            voter.setPassword(passwordEncoder.encode(voter.getPassword()));
        }

        Voter savedVoter = voterRepository.save(voter);
        log.info("Voter created successfully: {}", savedVoter.getEmail());
        return savedVoter;
    }

    /**
     * Updates an existing voter.
     *
     * @param id the voter ID
     * @param voterDetails the updated voter details
     * @return the updated voter
     * @throws ResourceNotFoundException if voter not found
     * @throws DuplicateResourceException if email or PESEL already exists for another voter
     */
    @Transactional
    public Voter updateVoter(Long id, Voter voterDetails) {
        log.info("Updating voter with ID: {}", id);

        Voter voter = getVoterById(id);

        // Check if new email already exists for another voter
        if (!voter.getEmail().equals(voterDetails.getEmail()) &&
            voterRepository.existsByEmail(voterDetails.getEmail())) {
            log.warn("Cannot update voter: email already exists: {}", voterDetails.getEmail());
            throw new DuplicateResourceException("Voter", "email", voterDetails.getEmail());
        }

        // Check if new PESEL already exists for another voter
        if (voterDetails.getPesel() != null &&
            !voterDetails.getPesel().equals(voter.getPesel()) &&
            voterRepository.existsByPesel(voterDetails.getPesel())) {
            log.warn("Cannot update voter: PESEL already exists: {}", voterDetails.getPesel());
            throw new DuplicateResourceException("Voter", "PESEL", voterDetails.getPesel());
        }

        // Update fields
        voter.setEmail(voterDetails.getEmail());
        voter.setFirstName(voterDetails.getFirstName());
        voter.setLastName(voterDetails.getLastName());
        voter.setPesel(voterDetails.getPesel());
        voter.setRole(voterDetails.getRole());
        voter.setActive(voterDetails.getActive());

        // Update password only if provided and not already encoded
        if (voterDetails.getPassword() != null && !voterDetails.getPassword().isEmpty()) {
            if (!voterDetails.getPassword().startsWith("$2a$")) {
                voter.setPassword(passwordEncoder.encode(voterDetails.getPassword()));
            } else {
                voter.setPassword(voterDetails.getPassword());
            }
        }

        Voter updatedVoter = voterRepository.save(voter);
        log.info("Voter updated successfully: {}", updatedVoter.getEmail());
        return updatedVoter;
    }

    /**
     * Deletes a voter by ID.
     *
     * @param id the voter ID
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional
    public void deleteVoter(Long id) {
        log.info("Deleting voter with ID: {}", id);

        Voter voter = getVoterById(id);
        voterRepository.delete(voter);

        log.info("Voter deleted successfully: {}", voter.getEmail());
    }

    /**
     * Activates a voter account.
     *
     * @param id the voter ID
     * @return the updated voter
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional
    public Voter activateVoter(Long id) {
        log.info("Activating voter with ID: {}", id);

        Voter voter = getVoterById(id);
        voter.setActive(true);
        Voter activatedVoter = voterRepository.save(voter);

        log.info("Voter activated successfully: {}", activatedVoter.getEmail());
        return activatedVoter;
    }

    /**
     * Deactivates a voter account.
     *
     * @param id the voter ID
     * @return the updated voter
     * @throws ResourceNotFoundException if voter not found
     */
    @Transactional
    public Voter deactivateVoter(Long id) {
        log.info("Deactivating voter with ID: {}", id);

        Voter voter = getVoterById(id);
        voter.setActive(false);
        Voter deactivatedVoter = voterRepository.save(voter);

        log.info("Voter deactivated successfully: {}", deactivatedVoter.getEmail());
        return deactivatedVoter;
    }

    /**
     * Checks if a voter exists by email.
     *
     * @param email the email to check
     * @return true if voter exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return voterRepository.existsByEmail(email);
    }

    /**
     * Checks if a voter exists by PESEL.
     *
     * @param pesel the PESEL to check
     * @return true if voter exists
     */
    @Transactional(readOnly = true)
    public boolean existsByPesel(String pesel) {
        return voterRepository.existsByPesel(pesel);
    }
}
