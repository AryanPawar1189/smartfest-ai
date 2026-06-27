package com.smartfest.model;

/**
 * Holds an Event paired with its cosine similarity score.
 *
 * Used by the Recommendation API to rank events for a user.
 * The score comes from cosine_similarity(userProfileVector, eventTfIdfVector).
 * A higher score means the event is a better match for this user.
 *
 * Implements Comparable<EventScore> so Java Collections.sort() can sort them.
 *
 * Java features used: Comparable<T> (Generics), natural ordering
 */
public class EventScore implements Comparable<EventScore> {

    private Event  event;
    private double score;    // cosine similarity: 0.0 (no match) to 1.0 (perfect match)

    // ── Constructors ──────────────────────────────────────────────

    public EventScore() {}

    public EventScore(Event event, double score) {
        this.event = event;
        this.score = score;
    }

    // ── Comparable implementation (descending by score) ───────────

    @Override
    public int compareTo(EventScore other) {
        // Reversed so highest score comes first in sorted lists
        return Double.compare(other.score, this.score);
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public Event  getEvent()               { return event; }
    public void   setEvent(Event event)    { this.event = event; }

    public double getScore()               { return score; }
    public void   setScore(double score)   { this.score = score; }

    @Override
    public String toString() {
        return "EventScore{event=" + event.getTitle() +
               ", score=" + String.format("%.4f", score) + "}";
    }
}
