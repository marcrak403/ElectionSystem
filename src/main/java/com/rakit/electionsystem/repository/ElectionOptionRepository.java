package com.rakit.electionsystem.repository;

import com.rakit.electionsystem.model.ElectionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ElectionOption entity.
 * Provides database operations for election option management.
 */
@Repository
public interface ElectionOptionRepository extends JpaRepository<ElectionOption, Long> {

    /**
     * Finds all options for a specific election, ordered by display order.
     *
     * @param electionId the election ID
     * @return list of election options
     */
    List<ElectionOption> findByElectionIdOrderByDisplayOrderAsc(Long electionId);

    /**
     * Counts the number of options for a specific election.
     *
     * @param electionId the election ID
     * @return number of options
     */
    long countByElectionId(Long electionId);
}
