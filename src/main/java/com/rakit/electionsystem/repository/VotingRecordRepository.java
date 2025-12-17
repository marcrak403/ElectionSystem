package com.rakit.electionsystem.repository;

import com.rakit.electionsystem.model.VotingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for VotingRecord entity.
 * Provides database operations for voting record management.
 */
@Repository
public interface VotingRecordRepository extends JpaRepository<VotingRecord, Long> {

    /**
     * Checks if a voter has already voted in a specific election.
     *
     * @param voterId the voter ID
     * @param electionId the election ID
     * @return true if the voter has already voted in this election
     */
    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);

    /**
     * Finds a voting record for a specific voter and election.
     *
     * @param voterId the voter ID
     * @param electionId the election ID
     * @return Optional containing the voting record if found
     */
    Optional<VotingRecord> findByVoterIdAndElectionId(Long voterId, Long electionId);

    /**
     * Finds all voting records for a specific voter.
     *
     * @param voterId the voter ID
     * @return list of voting records
     */
    List<VotingRecord> findByVoterId(Long voterId);

    /**
     * Finds all voting records for a specific election.
     *
     * @param electionId the election ID
     * @return list of voting records
     */
    List<VotingRecord> findByElectionId(Long electionId);

    /**
     * Counts the number of voters who participated in a specific election.
     *
     * @param electionId the election ID
     * @return number of unique voters
     */
    long countByElectionId(Long electionId);
}
