package com.rakit.electionsystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing an election in the system.
 * Contains election details, options, and tracks voting records.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Election name is required")
    @Size(max = 255, message = "Election name must not exceed 255 characters")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ElectionStatus status = ElectionStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ElectionOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VotingRecord> votingRecords = new ArrayList<>();

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vote> votes = new ArrayList<>();

    /**
     * Lifecycle callback to set createdAt and updatedAt timestamps before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to update updatedAt timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an election option to this election.
     * Manages the bidirectional relationship.
     *
     * @param option the election option to add
     */
    public void addOption(ElectionOption option) {
        options.add(option);
        option.setElection(this);
    }

    /**
     * Removes an election option from this election.
     * Manages the bidirectional relationship.
     *
     * @param option the election option to remove
     */
    public void removeOption(ElectionOption option) {
        options.remove(option);
        option.setElection(null);
    }

    /**
     * Adds a voting record to this election.
     * Manages the bidirectional relationship.
     *
     * @param votingRecord the voting record to add
     */
    public void addVotingRecord(VotingRecord votingRecord) {
        votingRecords.add(votingRecord);
        votingRecord.setElection(this);
    }

    /**
     * Removes a voting record from this election.
     * Manages the bidirectional relationship.
     *
     * @param votingRecord the voting record to remove
     */
    public void removeVotingRecord(VotingRecord votingRecord) {
        votingRecords.remove(votingRecord);
        votingRecord.setElection(null);
    }

    /**
     * Adds a vote to this election.
     * Manages the bidirectional relationship.
     *
     * @param vote the vote to add
     */
    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setElection(this);
    }

    /**
     * Removes a vote from this election.
     * Manages the bidirectional relationship.
     *
     * @param vote the vote to remove
     */
    public void removeVote(Vote vote) {
        votes.remove(vote);
        vote.setElection(null);
    }

    /**
     * Checks if the election is currently active.
     *
     * @return true if the election status is ACTIVE and current time is between start and end dates
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == ElectionStatus.ACTIVE &&
               !now.isBefore(startDate) &&
               !now.isAfter(endDate);
    }

    /**
     * Checks if the election is closed.
     *
     * @return true if the election status is CLOSED
     */
    public boolean isClosed() {
        return status == ElectionStatus.CLOSED;
    }

    /**
     * Checks if the election is in draft status.
     *
     * @return true if the election status is DRAFT
     */
    public boolean isDraft() {
        return status == ElectionStatus.DRAFT;
    }

    /**
     * Checks if the election can be modified.
     * Elections can only be modified when they are in DRAFT status.
     *
     * @return true if the election can be modified
     */
    public boolean canBeModified() {
        return status == ElectionStatus.DRAFT;
    }

    /**
     * Gets the total number of votes cast in this election.
     *
     * @return total vote count
     */
    public long getTotalVotes() {
        return votes.size();
    }

    /**
     * Gets the total number of unique voters who participated in this election.
     *
     * @return total voter count
     */
    public long getTotalVoters() {
        return votingRecords.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Election election = (Election) o;
        return Objects.equals(id, election.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Election{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
