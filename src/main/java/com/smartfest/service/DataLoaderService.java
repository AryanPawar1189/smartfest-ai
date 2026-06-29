package com.smartfest.service;

import com.smartfest.model.Event;
import com.smartfest.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads all data files at startup and keeps them in memory.
 * Other services inject this to access events, users, transactions.
 *
 * @PostConstruct runs loadData() automatically after Spring
 * creates this object — before any API calls arrive.
 */
@Service
public class DataLoaderService {

    // These lists are the in-memory "database" for the whole project
    private List<Event> events = new ArrayList<>();
    private List<User>  users  = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        loadEvents();
        loadUsers();
    }

    // ── Load events.csv ───────────────────────────────────────────

    private void loadEvents() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/events.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean firstLine = true; // skip header row

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                // Split by comma, but ignore commas inside quotes
                // (tags field has commas: "rock,live,electric")
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (parts.length >= 12) {
                    Event event = new Event();
                    event.setEventId(parts[0].trim());
                    event.setTitle(parts[1].trim());
                    event.setCategory(parts[2].trim());
                    event.setDescription(parts[3].trim());

                    // Tags: remove quotes, split by comma into List<String>
                    String tagsRaw = parts[4].replace("\"", "").trim();
                    event.setTags(Arrays.asList(tagsRaw.split(",")));

                    event.setVenue(parts[5].trim());
                    event.setDate(parts[6].trim());
                    event.setTime(parts[7].trim());

                    events.add(event);
                }
            }
            reader.close();
            System.out.println("✅ Loaded " + events.size() + " events");

        } catch (Exception e) {
            System.err.println("❌ Failed to load events.csv: " + e.getMessage());
        }
    }

    // ── Load users.json ───────────────────────────────────────────

    private void loadUsers() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/users.json");

            // ObjectMapper is Jackson's JSON parser — reads JSON → Java objects
            ObjectMapper mapper = new ObjectMapper();

            // TypeReference tells Jackson the exact type to deserialize into
            // We need List<User>, not just List (which would lose type info)
            users = mapper.readValue(is, new TypeReference<List<User>>() {});

            System.out.println("✅ Loaded " + users.size() + " users");

        } catch (Exception e) {
            System.err.println("❌ Failed to load users.json: " + e.getMessage());
        }
    }

    // ── Getters for other services ────────────────────────────────

    public List<Event> getEvents() { return events; }
    public List<User>  getUsers()  { return users; }

    public User getUserById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public Event getEventById(String eventId) {
        return events.stream()
                .filter(e -> e.getEventId().equals(eventId))
                .findFirst()
                .orElse(null);
    }
}