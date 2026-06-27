package com.smartfest.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String eventId) {
        super("Event not found: " + eventId);
    }
}
