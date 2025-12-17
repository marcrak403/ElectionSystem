package com.rakit.electionsystem.exception;

/**
 * Exception thrown when a voter attempts to vote in an election they've already voted in.
 */
public class AlreadyVotedException extends RuntimeException {

    public AlreadyVotedException(String message) {
        super(message);
    }

    public AlreadyVotedException(Long voterId, Long electionId) {
        super(String.format("Voter with ID %d has already voted in election with ID %d", voterId, electionId));
    }
}
