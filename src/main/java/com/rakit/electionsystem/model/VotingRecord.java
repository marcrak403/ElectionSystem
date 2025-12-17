package com.rakit.electionsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a record that a voter participated in an election.
 * This entity serves as a bridge between voters and elections, recording that a voter
 * has cast a vote, but WITHOUT revealing which option they chose.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    @NotNull(message = "Voter is required")
    private Voter voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    @NotNull(message = "Election is required")
    private Election election;

    @Column(name = "voted_at", nullable = false, updatable = false)
    private LocalDateTime votedAt;

    /**
     * Lifecycle callback to set votedAt timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VotingRecord that = (VotingRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VotingRecord{" +
                "id=" + id +
                ", votedAt=" + votedAt +
                '}';
    }
}
