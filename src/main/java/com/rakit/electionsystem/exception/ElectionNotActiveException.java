package com.rakit.electionsystem.exception;

/**
 * Exception thrown when attempting to vote in an election that is not active.
 */
public class ElectionNotActiveException extends RuntimeException {

    public ElectionNotActiveException(String message) {
        super(message);
    }

    public ElectionNotActiveException(Long electionId) {
        super(String.format("Election with ID %d is not active", electionId));
    }
}
