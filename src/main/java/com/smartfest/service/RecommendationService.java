package com.smartfest.service;

import com.smartfest.exception.UserNotFoundException;
import com.smartfest.model.Event;
import com.smartfest.model.EventScore;
import com.smartfest.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    // Spring injects these automatically (Constructor Injection)
    private final DataLoaderService dataLoaderService;
    private final TfIdfEngine tfidfEngine;

    public RecommendationService(DataLoaderService dataLoaderService,
                                  TfIdfEngine tfidfEngine) {
        this.dataLoaderService = dataLoaderService;
        this.tfidfEngine       = tfidfEngine;
    }

    // Build TF-IDF index right after DataLoaderService has loaded events
    @PostConstruct
    public void init() {
        List<Event> events = dataLoaderService.getEvents();
        tfidfEngine.buildIndex(events);
        System.out.println("✅ RecommendationService ready");
    }

    // ── Main API method ───────────────────────────────────────────

    public List<EventScore> recommend(String userId, int topN) {

        // 1. Get user — throws UserNotFoundException if not found
        User user = dataLoaderService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        // 2. Build user profile from their browsing history
        Map<String, Double> userProfile =
            tfidfEngine.buildUserProfile(user.getBrowsingHistory());

        // 3. If user has no history, return popular events fallback
        if (userProfile.isEmpty()) {
            return getFallbackRecommendations(topN);
        }

        List<Event> allEvents = dataLoaderService.getEvents();

        // 4. Stream pipeline: score every event → sort → return top-N
        return allEvents.stream()

            // Skip events already registered by this user
            .filter(event ->
                !user.getRegisteredEvents().contains(event.getEventId()))

            // Skip events with no TF-IDF vector
            .filter(event -> tfidfEngine.hasVector(event.getEventId()))

            // Score each event against user profile
            .map(event -> {
                Map<String, Double> eventVector =
                    tfidfEngine.getVector(event.getEventId());
                double score = cosineSimilarity(userProfile, eventVector);
                return new EventScore(event, score);
            })

            // Drop events with negligible similarity (not relevant)
            .filter(es -> es.getScore() > 0.01)

            // Sort highest score first
            .sorted(Comparator.comparingDouble(EventScore::getScore).reversed())

            // Keep only top-N
            .limit(topN)

            .collect(Collectors.toList());
    }

    // ── Cosine Similarity ─────────────────────────────────────────

    /**
     * Measures how similar two TF-IDF vectors are.
     * Returns: 0.0 (completely different) to 1.0 (identical)
     *
     * Formula: (A · B) / (|A| × |B|)
     */
    private double cosineSimilarity(Map<String, Double> vectorA,
                                     Map<String, Double> vectorB) {
        if (vectorA.isEmpty() || vectorB.isEmpty()) return 0.0;

        double dotProduct  = 0.0;
        double magnitudeA  = 0.0;
        double magnitudeB  = 0.0;

        // Dot product: only iterate words in A, check if they exist in B
        // (much faster than nested loops over both)
        for (Map.Entry<String, Double> entry : vectorA.entrySet()) {
            double a = entry.getValue();
            double b = vectorB.getOrDefault(entry.getKey(), 0.0);
            dotProduct += a * b;
            magnitudeA += a * a;
        }

        // Magnitude of B (iterate all of B's words)
        for (double val : vectorB.values()) {
            magnitudeB += val * val;
        }

        double denominator = Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB);
        if (denominator == 0) return 0.0;

        return dotProduct / denominator;
    }

    // ── Fallback: return first N events for new users ─────────────

    private List<EventScore> getFallbackRecommendations(int topN) {
        return dataLoaderService.getEvents().stream()
                .limit(topN)
                .map(event -> new EventScore(event, 0.5)) // neutral score
                .collect(Collectors.toList());
    }
}