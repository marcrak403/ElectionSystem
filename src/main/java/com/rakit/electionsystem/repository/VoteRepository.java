package com.rakit.electionsystem.repository;

import com.rakit.electionsystem.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for Vote entity.
 * Provides database operations for anonymous vote management.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /**
     * Counts total votes for a specific election.
     *
     * @param electionId the election ID
     * @return total number of votes
     */
    long countByElectionId(Long electionId);

    /**
     * Counts votes for a specific election option.
     *
     * @param electionOptionId the election option ID
     * @return number of votes for this option
     */
    long countByElectionOptionId(Long electionOptionId);

    /**
     * Counts votes for a specific option within a specific election.
     *
     * @param electionId the election ID
     * @param electionOptionId the election option ID
     * @return number of votes for this option in this election
     */
    long countByElectionIdAndElectionOptionId(Long electionId, Long electionOptionId);

    /**
     * Gets vote counts grouped by election option for a specific election.
     *
     * @param electionId the election ID
     * @return list of objects containing election option ID and vote count
     */
    @Query("SELECT v.electionOption.id as optionId, COUNT(v) as voteCount " +
           "FROM Vote v WHERE v.election.id = ?1 GROUP BY v.electionOption.id")
    List<Map<String, Object>> countVotesByElectionOption(Long electionId);
}
