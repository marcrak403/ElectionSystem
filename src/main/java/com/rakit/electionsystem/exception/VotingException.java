package com.rakit.electionsystem.exception;

/**
 * Exception thrown when there's an issue with the voting process.
 */
public class VotingException extends RuntimeException {

    public VotingException(String message) {
        super(message);
    }
}
