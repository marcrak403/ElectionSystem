package com.rakit.electionsystem.exception;

/**
 * Exception thrown when a JWT token is invalid or expired.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Invalid or expired token");
    }
}
