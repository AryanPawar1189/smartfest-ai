package com.smartfest.service;

import com.smartfest.model.FraudResult;
import com.smartfest.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fraud Detection Service — Rule Engine + K-Means Clustering
 *
 * Two-phase detection:
 *   Phase 1 (Rule Engine):  Fast deterministic checks — amount threshold,
 *                           rapid transactions, velocity patterns.
 *   Phase 2 (K-Means):     Unsupervised anomaly detection — assign transaction
 *                           to a cluster; high centroid distance = suspicious.
 *
 * Combines both phases into a single risk score [0.0, 1.0].
 *
 * Java features used:
 *   - Multithreading:       ExecutorService (thread pool for parallel scoring)
 *   - ConcurrentHashMap:    Thread-safe cache of cluster assignments
 *   - Observer Pattern:     FraudAlertObserver notified when fraud is detected
 *   - Collections:          List<FraudAlertObserver>, List<String> (rule reasons)
 *   - Custom Exceptions:    Thrown on invalid input
 *
 * TODO (implementation in progress):
 *   - [ ] Implement RuleEngine.evaluate(Transaction, UserSpendingHistory)
 *   - [ ] Implement KMeansClusterer.train(List<Transaction>)
 *   - [ ] Implement KMeansClusterer.assignCluster(Transaction)
 *   - [ ] Wire DataLoaderService to preload transaction history
 */
@Service
public class FraudDetectionService {

    // ── Config (injected from application.properties) ─────────────
    @Value("${fraud.amount.threshold:10000}")
    private double amountThreshold;

    @Value("${fraud.thread.pool.size:4}")
    private int threadPoolSize;

    // ── Thread pool for parallel fraud scoring ────────────────────
    // ExecutorService manages a fixed number of reusable threads
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    // ── Thread-safe cluster assignment cache ──────────────────────
    // ConcurrentHashMap allows multiple threads to read/write without data corruption
    private final ConcurrentHashMap<String, Integer> clusterCache = new ConcurrentHashMap<>();

    // ── Observer pattern: list of fraud alert listeners ───────────
    // Observer Pattern: fraud service doesn't know WHO is listening — only THAT they exist
    private final List<FraudAlertObserver> observers = new ArrayList<>();

    // ── Observer interface (inner — will be extracted to own file later) ──

    public interface FraudAlertObserver {
        void onFraudDetected(FraudResult result, Transaction transaction);
    }

    public void addObserver(FraudAlertObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(FraudResult result, Transaction transaction) {
        // Lambda: forEach uses a lambda to call each observer
        observers.forEach(obs -> obs.onFraudDetected(result, transaction));
    }

    // ── Main entry point ──────────────────────────────────────────

    /**
     * Run fraud detection on a transaction.
     * Returns a FraudResult with risk score and recommendation.
     */
    public FraudResult check(Transaction transaction) {

        // Phase 1: Rule-based detection (fast, synchronous)
        List<String> triggeredRules = evaluateRules(transaction);
        double ruleScore = computeRuleScore(triggeredRules);

        // Phase 2: K-Means anomaly detection (TODO: async via ExecutorService)
        // For now, placeholder logic until KMeansClusterer is implemented
        int cluster = 0;
        double distanceFromCentroid = 0.0;
        double kmeansScore = 0.0;

        // Combined risk score (60% rules, 40% k-means)
        double finalScore = (0.6 * ruleScore) + (0.4 * kmeansScore);
        boolean isFraud = finalScore >= 0.5 || !triggeredRules.isEmpty();

        FraudResult result = new FraudResult(
                transaction.getTransactionId(),
                isFraud,
                finalScore,
                triggeredRules,
                cluster,
                distanceFromCentroid
        );

        // Observer pattern: notify all registered listeners
        if (isFraud) {
            notifyObservers(result, transaction);
        }

        return result;
    }

    // ── Rule Engine ───────────────────────────────────────────────

    /**
     * Evaluates a set of fraud detection rules against the transaction.
     * Returns list of rule names that were triggered.
     */
    private List<String> evaluateRules(Transaction transaction) {
        List<String> triggered = new ArrayList<>();

        // Rule 1: Amount spike
        if (transaction.getAmount() > amountThreshold) {
            triggered.add("amount_spike");
        }

        // Rule 2: Zero/negative amount (invalid transaction)
        if (transaction.getAmount() <= 0) {
            triggered.add("invalid_amount");
        }

        // TODO: Add more rules:
        // Rule 3: rapid_txn  — more than 3 transactions in 2 minutes for this user
        // Rule 4: abnormal_pattern — amount > 5x user's average spend
        // Rule 5: new_device — transaction from a device never seen before

        return triggered;
    }

    private double computeRuleScore(List<String> triggeredRules) {
        // Each rule contributes 0.3 to the score, capped at 1.0
        return Math.min(1.0, triggeredRules.size() * 0.3);
    }
}
