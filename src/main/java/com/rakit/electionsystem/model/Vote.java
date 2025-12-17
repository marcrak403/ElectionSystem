package com.rakit.electionsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Entity representing an anonymous vote cast in an election.
 *
 * How anonymity is maintained:
 * When a voter casts a vote, two separate records are created:
 *    - A VotingRecord (links Voter to Election) - proves they voted
 *    - A Vote (links Election to ElectionOption) - records what was voted for
 *
 * This separation ensures that even with full database access, it's impossible to determine
 * which voter chose which option, while still preventing duplicate voting and maintaining
 * election integrity.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The election this vote belongs to.
     * Note: This relationship exists, but there is NO relationship to Voter.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    @NotNull(message = "Election is required")
    private Election election;

    /**
     * The election option that was chosen in this vote.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_option_id", nullable = false)
    @NotNull(message = "Election option is required")
    private ElectionOption electionOption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString without lazy relations

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                '}';
    }
}
