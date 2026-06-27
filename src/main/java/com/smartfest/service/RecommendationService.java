package com.smartfest.service;

import com.smartfest.exception.UserNotFoundException;
import com.smartfest.model.Event;
import com.smartfest.model.EventScore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommendation Service — TF-IDF + Cosine Similarity Engine
 *
 * Algorithm:
 *   1. At startup, load all events from CSV → compute TF-IDF vector per event
 *   2. On request, load user's browsing history → build user profile vector
 *      (average of TF-IDF vectors of browsed events)
 *   3. Compute cosine similarity between user profile and each event vector
 *   4. Sort descending by score → return top-N events
 *
 * Java features used:
 *   - Collections:   HashMap<String, Map<String, Double>> for TF-IDF index
 *   - Generics:      List<EventScore>, Map<String, Double>
 *   - Streams API:   .stream().map().sorted().limit().collect()
 *   - Lambdas:       Comparator.comparingDouble(EventScore::getScore).reversed()
 *   - File I/O:      Loading events.csv at startup
 *   - Serialization: Caching TF-IDF index to disk
 *
 * TODO (implementation in progress):
 *   - [ ] Implement DataLoaderService to read events.csv
 *   - [ ] Implement TfIdfEngine.buildIndex(List<Event>)
 *   - [ ] Implement TfIdfEngine.buildUserProfile(List<String> eventIds)
 *   - [ ] Implement CosineSimilarity.compute(Map<String, Double>, Map<String, Double>)
 */
@Service
public class RecommendationService {

    // TF-IDF index: eventId → (word → tfidf score)
    // Using nested HashMap — a core Java Collections feature
    private final Map<String, Map<String, Double>> tfidfIndex = new HashMap<>();
    private final List<Event> allEvents = new ArrayList<>();

    /**
     * Returns top-N recommended events for the given user.
     *
     * Uses Java Streams + Lambdas for elegant pipeline processing:
     *   events → compute scores → sort descending → take top-N → collect to list
     */
    public List<EventScore> recommend(String userId, int topN) {

        // TODO: Replace with real user lookup from DataLoaderService
        if (userId == null || userId.isBlank()) {
            throw new UserNotFoundException(userId);
        }

        // TODO: Build real userProfile vector from browsing history
        Map<String, Double> userProfile = buildMockUserProfile();

        // Stream pipeline: score every event, sort, return top-N
        // This is where Streams API + Lambdas shine
        return allEvents.stream()
                .filter(event -> tfidfIndex.containsKey(event.getEventId()))
                .map(event -> {
                    Map<String, Double> eventVector = tfidfIndex.get(event.getEventId());
                    double score = cosineSimilarity(userProfile, eventVector);
                    return new EventScore(event, score);
                })
                .sorted(Comparator.comparingDouble(EventScore::getScore).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Cosine similarity between two TF-IDF vectors.
     *
     * formula: (A · B) / (|A| * |B|)
     * where A · B = sum of (a_i * b_i) for all shared words
     */
    private double cosineSimilarity(Map<String, Double> vectorA,
                                     Map<String, Double> vectorB) {
        if (vectorA.isEmpty() || vectorB.isEmpty()) return 0.0;

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        // Only iterate over words in A and check if they exist in B (efficiency)
        for (Map.Entry<String, Double> entry : vectorA.entrySet()) {
            double a = entry.getValue();
            double b = vectorB.getOrDefault(entry.getKey(), 0.0);
            dotProduct += a * b;
            magnitudeA += a * a;
        }

        for (double val : vectorB.values()) {
            magnitudeB += val * val;
        }

        double denominator = Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB);
        return (denominator == 0) ? 0.0 : dotProduct / denominator;
    }

    // ── Mock helpers (to be replaced with real DataLoaderService) ──────

    private Map<String, Double> buildMockUserProfile() {
        // Placeholder: returns a fixed vector representing "music + dance" preference
        Map<String, Double> profile = new HashMap<>();
        profile.put("music", 0.8);
        profile.put("dance", 0.6);
        profile.put("live", 0.5);
        profile.put("performance", 0.4);
        return profile;
    }
}
