package com.rakit.electionsystem.repository;

import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Election entity.
 * Provides database operations for election management.
 */
@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    /**
     * Finds all elections with a specific status.
     *
     * @param status the election status to filter by
     * @return list of elections with the given status
     */
    List<Election> findByStatus(ElectionStatus status);

    /**
     * Finds all active elections (status = ACTIVE and within date range).
     *
     * @param now current timestamp
     * @return list of active elections
     */
    @Query("SELECT e FROM Election e WHERE e.status = 'ACTIVE' AND e.startDate <= ?1 AND e.endDate >= ?1")
    List<Election> findActiveElections(LocalDateTime now);

    /**
     * Finds all elections ordered by start date descending.
     *
     * @return list of all elections ordered by start date
     */
    List<Election> findAllByOrderByStartDateDesc();

    /**
     * Finds elections that should be automatically closed (ended and still active).
     *
     * @param now current timestamp
     * @return list of elections that should be closed
     */
    @Query("SELECT e FROM Election e WHERE e.status = 'ACTIVE' AND e.endDate < ?1")
    List<Election> findElectionsToClose(LocalDateTime now);
}
