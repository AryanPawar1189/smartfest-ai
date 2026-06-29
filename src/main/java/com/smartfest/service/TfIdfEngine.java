package com.smartfest.service;

import com.smartfest.model.Event;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TF-IDF Engine — converts event descriptions into numerical vectors.
 *
 * Step 1: buildIndex(events)  — call once at startup
 * Step 2: getVector(eventId)  — get the TF-IDF vector for any event
 * Step 3: buildUserProfile()  — average vectors of events user has browsed
 */
@Component
public class TfIdfEngine {

    // The main index: eventId → (word → tfidf score)
    // This is the nested HashMap we discussed in the Collections lesson
    private Map<String, Map<String, Double>> tfidfIndex = new HashMap<>();

    // IDF scores: word → idf score (shared across all events)
    private Map<String, Double> idfScores = new HashMap<>();

    // All unique words across all event descriptions
    private Set<String> vocabulary = new HashSet<>();

    // ── Step 1: Build the entire TF-IDF index ────────────────────

    public void buildIndex(List<Event> events) {
        System.out.println("🔧 Building TF-IDF index for " + events.size() + " events...");

        // 1a. Build vocabulary and TF scores for each event
        // tempTf stores raw TF scores before multiplying by IDF
        Map<String, Map<String, Double>> tempTf = new HashMap<>();

        for (Event event : events) {
            // Tokenize: split description into individual words
            List<String> words = tokenize(event.getDescription());

            // Also add tags — they're strong signals of what the event is about
            // "bhangra" in tags is just as meaningful as in description
            if (event.getTags() != null) {
                for (String tag : event.getTags()) {
                    words.addAll(tokenize(tag));
                }
            }

            // Calculate TF for this event
            Map<String, Double> tf = calculateTF(words);
            tempTf.put(event.getEventId(), tf);

            // Add all words to vocabulary
            vocabulary.addAll(tf.keySet());
        }

        // 1b. Calculate IDF for every word in vocabulary
        // IDF needs to know about ALL events, so we do it after loading all TF
        idfScores = calculateIDF(events, tempTf);

        // 1c. Multiply TF × IDF to get final TF-IDF vectors
        for (Event event : events) {
            Map<String, Double> tf    = tempTf.get(event.getEventId());
            Map<String, Double> tfidf = new HashMap<>();

            for (Map.Entry<String, Double> entry : tf.entrySet()) {
                String word     = entry.getKey();
                double tfScore  = entry.getValue();
                double idfScore = idfScores.getOrDefault(word, 0.0);
                double tfidfScore = tfScore * idfScore;

                // Only store non-zero scores (saves memory)
                if (tfidfScore > 0) {
                    tfidf.put(word, tfidfScore);
                }
            }

            tfidfIndex.put(event.getEventId(), tfidf);
        }

        System.out.println("✅ TF-IDF index built. Vocabulary size: " + vocabulary.size() + " words");
    }

    // ── Step 2: Tokenize — split text into clean words ────────────

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();

        return Arrays.stream(text.toLowerCase()     // lowercase everything
                .replaceAll("[^a-z0-9\\s-]", " ")  // remove punctuation
                .split("\\s+"))                     // split by whitespace
                .filter(w -> w.length() > 2)        // remove tiny words ("a", "of", "in")
                .filter(w -> !isStopWord(w))         // remove common English stop words
                .collect(Collectors.toList());
    }

    // Stop words: words so common they mean nothing
    // "the", "and", "is" appear in EVERY description — useless for TF-IDF
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "and", "for", "with", "will", "are", "was", "this",
        "that", "have", "from", "they", "been", "their", "has",
        "can", "all", "its", "also", "both", "each", "more",
        "where", "which", "into", "than", "then", "when", "who",
        "open", "use", "used", "using", "includes", "include",
        "including", "based", "across", "within", "without"
    ));

    private boolean isStopWord(String word) {
        return STOP_WORDS.contains(word);
    }

    // ── Calculate TF for one event ────────────────────────────────

    private Map<String, Double> calculateTF(List<String> words) {
        // Count occurrences of each word
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        int totalWords = words.size();
        Map<String, Double> tf = new HashMap<>();

        // TF = count / totalWords
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            tf.put(entry.getKey(), (double) entry.getValue() / totalWords);
        }

        return tf;
    }

    // ── Calculate IDF for every word ─────────────────────────────

    private Map<String, Double> calculateIDF(List<Event> events,
                                              Map<String, Map<String, Double>> tempTf) {
        int totalEvents = events.size();
        Map<String, Double> idf = new HashMap<>();

        // For each word in vocabulary, count how many events contain it
        for (String word : vocabulary) {
            int eventsWithWord = 0;

            for (Event event : events) {
                Map<String, Double> tf = tempTf.get(event.getEventId());
                if (tf != null && tf.containsKey(word)) {
                    eventsWithWord++;
                }
            }

            // IDF = log(totalEvents / eventsWithWord)
            // Adding 1 to eventsWithWord avoids log(infinity) if word is in no events
            if (eventsWithWord > 0) {
                idf.put(word, Math.log((double) totalEvents / eventsWithWord));
            }
        }

        return idf;
    }

    // ── Step 3: Build user profile from browsing history ─────────

    /**
     * Creates a user preference vector by averaging the TF-IDF vectors
     * of all events the user has browsed.
     *
     * If u_001 browsed [Hackathon, Robotics, App Dev]:
     *   Hackathon vector:  {coding: 0.7, programming: 0.6, hackathon: 0.8}
     *   Robotics vector:   {robotics: 0.9, engineering: 0.7, arduino: 0.6}
     *   AppDev vector:     {programming: 0.8, mobile: 0.7, flutter: 0.6}
     *
     *   Average (profile): {coding: 0.23, programming: 0.47, hackathon: 0.27,
     *                        robotics: 0.30, engineering: 0.23, arduino: 0.20,
     *                        mobile: 0.23, flutter: 0.20}
     *
     * This profile vector represents the user's interests as a single vector
     * that captures themes from ALL their browsed events.
     */
    public Map<String, Double> buildUserProfile(List<String> browsingHistory) {
        if (browsingHistory == null || browsingHistory.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Double> profileSum = new HashMap<>();
        int validEvents = 0;

        for (String eventId : browsingHistory) {
            Map<String, Double> eventVector = tfidfIndex.get(eventId);
            if (eventVector == null) continue;

            // Add this event's vector to the running sum
            for (Map.Entry<String, Double> entry : eventVector.entrySet()) {
                profileSum.merge(entry.getKey(), entry.getValue(), Double::sum);
                // merge(key, value, fn): if key exists, apply fn(old, new); else put value
                // Double::sum is a method reference for (a, b) -> a + b
            }
            validEvents++;
        }

        if (validEvents == 0) return new HashMap<>();

        // Divide each sum by number of events to get the average
        final int count = validEvents;
        profileSum.replaceAll((word, sumScore) -> sumScore / count);
        // replaceAll: for every entry, replace value with fn(key, value)

        return profileSum;
    }

    // ── Getters ───────────────────────────────────────────────────

    public Map<String, Double> getVector(String eventId) {
        return tfidfIndex.getOrDefault(eventId, new HashMap<>());
    }

    public boolean hasVector(String eventId) {
        return tfidfIndex.containsKey(eventId);
    }

    public int getVocabularySize() {
        return vocabulary.size();
    }
}