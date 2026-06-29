package com.smartfest.model;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a festival event.
 *
 * Serializable so TF-IDF vectors computed from events can be cached to disk
 * (Java Serialization feature), avoiding recomputation on every restart.
 *
 * Java features used: Generics (List<String>), Serialization
 */
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String eventId;
    private String title;
    private String description;     // raw text used for TF-IDF vectorisation
    private String category;        // "Music", "Dance", "Quiz", "Comedy", etc.
    private List<String> tags;      // ["rock", "live", "electric-guitar"]
    private String venue;
    private String date;
    private String time;
    private int capacity;
    private double price;

    // ── Constructors ──────────────────────────────────────────────

    public Event() {}

    public Event(String eventId, String title, String description,
                 String category, List<String> tags,
                 String venue, String date, String time,
                 int capacity, double price) {
        this.eventId     = eventId;
        this.title       = title;
        this.description = description;
        this.category    = category;
        this.tags        = tags;
        this.venue       = venue;
        this.date        = date;
        this.time        = time;
        this.capacity    = capacity;
        this.price       = price;
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public String getEventId()                  { return eventId; }
    public void   setEventId(String eventId)    { this.eventId = eventId; }

    public String getTitle()                    { return title; }
    public void   setTitle(String title)        { this.title = title; }

    public String getDescription()              { return description; }
    public void   setDescription(String desc)   { this.description = desc; }

    public String getCategory()                 { return category; }
    public void   setCategory(String category)  { this.category = category; }

    public List<String> getTags()               { return tags; }
    public void   setTags(List<String> tags)    { this.tags = tags; }

    public String getVenue()                    { return venue; }
    public void   setVenue(String venue)        { this.venue = venue; }

    public String getDate()                     { return date; }
    public void   setDate(String date)          { this.date = date; }

    public String getTime()                     { return time; }
    public void   setTime(String time)          { this.time = time; }

    public int    getCapacity()                 { return capacity; }
    public void   setCapacity(int capacity)     { this.capacity = capacity; }

    public double getPrice()                    { return price; }
    public void   setPrice(double price)        { this.price = price; }

    @Override
    public String toString() {
        return "Event{id='" + eventId + "', title='" + title +
               "', category='" + category + "'}";
    }
}
