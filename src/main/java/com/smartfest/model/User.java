package com.smartfest.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a festival participant / user.
 *
 * The browsingHistory list drives the TF-IDF user profile:
 * we look up the events the user has viewed, aggregate their
 * TF-IDF vectors, and use the result as the user's preference vector.
 *
 * Java features used: Generics (List<String>, Map<String, Integer>)
 */
public class User {

    private String userId;
    private String name;
    private String email;
    private List<String> browsingHistory;       // list of eventIds the user has viewed
    private List<String> registeredEvents;      // events the user is already registered for
    private Map<String, Integer> categoryAffinityScores; // e.g. {"Music": 8, "Dance": 3}

    // ── Constructors ──────────────────────────────────────────────

    public User() {}

    public User(String userId, String name, String email,
                List<String> browsingHistory,
                List<String> registeredEvents,
                Map<String, Integer> categoryAffinityScores) {
        this.userId                  = userId;
        this.name                    = name;
        this.email                   = email;
        this.browsingHistory         = browsingHistory;
        this.registeredEvents        = registeredEvents;
        this.categoryAffinityScores  = categoryAffinityScores;
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public String getUserId()                         { return userId; }
    public void   setUserId(String userId)            { this.userId = userId; }

    public String getName()                           { return name; }
    public void   setName(String name)                { this.name = name; }

    public String getEmail()                          { return email; }
    public void   setEmail(String email)              { this.email = email; }

    public List<String> getBrowsingHistory()          { return browsingHistory; }
    public void setBrowsingHistory(List<String> h)    { this.browsingHistory = h; }

    public List<String> getRegisteredEvents()         { return registeredEvents; }
    public void setRegisteredEvents(List<String> e)   { this.registeredEvents = e; }

    public Map<String, Integer> getCategoryAffinityScores()          { return categoryAffinityScores; }
    public void setCategoryAffinityScores(Map<String, Integer> s)    { this.categoryAffinityScores = s; }

    @Override
    public String toString() {
        return "User{id='" + userId + "', name='" + name + "'}";
    }
}
