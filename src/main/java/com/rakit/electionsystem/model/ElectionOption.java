package com.rakit.electionsystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
 * Entity representing an option in an election.
 * Voters can choose from these options when casting their votes.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    @NotNull(message = "Election is required")
    private Election election;

    @Column(name = "option_title", nullable = false)
    @NotBlank(message = "Option title is required")
    @Size(max = 255, message = "Option title must not exceed 255 characters")
    private String optionTitle;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "electionOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vote> votes = new ArrayList<>();

    /**
     * Lifecycle callback to set createdAt timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Adds a vote to this election option.
     * Manages the bidirectional relationship.
     *
     * @param vote the vote to add
     */
    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setElectionOption(this);
    }

    /**
     * Removes a vote from this election option.
     * Manages the bidirectional relationship.
     *
     * @param vote the vote to remove
     */
    public void removeVote(Vote vote) {
        votes.remove(vote);
        vote.setElectionOption(null);
    }

    /**
     * Gets the number of votes cast for this option.
     *
     * @return vote count
     */
    public long getVoteCount() {
        return votes.size();
    }

    /**
     * Calculates the percentage of votes this option received.
     *
     * @param totalVotes total number of votes in the election
     * @return percentage (0-100)
     */
    public double getVotePercentage(long totalVotes) {
        if (totalVotes == 0) {
            return 0.0;
        }
        return (double) getVoteCount() / totalVotes * 100.0;
    }

    // equals and hashCode based on id

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElectionOption that = (ElectionOption) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ElectionOption{" +
                "id=" + id +
                ", optionTitle='" + optionTitle + '\'' +
                ", description='" + description + '\'' +
                ", displayOrder=" + displayOrder +
                ", createdAt=" + createdAt +
                '}';
    }
}
