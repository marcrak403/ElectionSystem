package com.rakit.electionsystem.repository;

import com.rakit.electionsystem.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Voter entity.
 * Provides database operations for voter management.
 */
@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {

    /**
     * Finds a voter by email address.
     *
     * @param email the email address to search for
     * @return Optional containing the voter if found
     */
    Optional<Voter> findByEmail(String email);

    /**
     * Finds a voter by PESEL number.
     *
     * @param pesel the PESEL number to search for
     * @return Optional containing the voter if found
     */
    Optional<Voter> findByPesel(String pesel);

    /**
     * Checks if a voter with the given email exists.
     *
     * @param email the email address to check
     * @return true if a voter with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a voter with the given PESEL exists.
     *
     * @param pesel the PESEL number to check
     * @return true if a voter with this PESEL exists
     */
    boolean existsByPesel(String pesel);
}
