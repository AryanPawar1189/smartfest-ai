package com.smartfest.exception;

/**
 * Thrown when a requested user is not found in the system.
 *
 * Java feature used: Custom Exceptions (extending RuntimeException)
 *
 * By extending RuntimeException (unchecked), callers don't need
 * to declare or catch it — Spring Boot's @ExceptionHandler picks it up.
 */
public class UserNotFoundException extends RuntimeException {

    private final String userId;

    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
