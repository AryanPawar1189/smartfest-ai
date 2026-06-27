package com.smartfest.model;

import java.util.List;

/**
 * Result returned by the Fraud Detection API.
 *
 * Contains the risk score, whether the transaction is flagged,
 * the specific rules that were triggered, and the K-Means cluster info.
 */
public class FraudResult {

    private String transactionId;
    private boolean fraud;
    private double riskScore;           // 0.0 (safe) → 1.0 (definitely fraud)
    private List<String> triggeredRules; // e.g. ["amount_spike", "rapid_txn"]
    private int assignedCluster;        // which K-Means cluster this transaction fell into
    private double distanceFromCentroid; // how far from cluster center — high = anomalous
    private String recommendation;      // "APPROVE" | "REVIEW" | "BLOCK"

    // ── Constructors ──────────────────────────────────────────────

    public FraudResult() {}

    public FraudResult(String transactionId, boolean fraud, double riskScore,
                       List<String> triggeredRules, int assignedCluster,
                       double distanceFromCentroid) {
        this.transactionId        = transactionId;
        this.fraud                = fraud;
        this.riskScore            = riskScore;
        this.triggeredRules       = triggeredRules;
        this.assignedCluster      = assignedCluster;
        this.distanceFromCentroid = distanceFromCentroid;
        this.recommendation       = deriveRecommendation(riskScore);
    }

    private String deriveRecommendation(double score) {
        if (score >= 0.7) return "BLOCK";
        if (score >= 0.4) return "REVIEW";
        return "APPROVE";
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public String getTransactionId()                         { return transactionId; }
    public void   setTransactionId(String id)                { this.transactionId = id; }

    public boolean isFraud()                                 { return fraud; }
    public void    setFraud(boolean fraud)                   { this.fraud = fraud; }

    public double  getRiskScore()                            { return riskScore; }
    public void    setRiskScore(double score)                { this.riskScore = score; }

    public List<String> getTriggeredRules()                  { return triggeredRules; }
    public void   setTriggeredRules(List<String> rules)      { this.triggeredRules = rules; }

    public int    getAssignedCluster()                       { return assignedCluster; }
    public void   setAssignedCluster(int c)                  { this.assignedCluster = c; }

    public double getDistanceFromCentroid()                  { return distanceFromCentroid; }
    public void   setDistanceFromCentroid(double d)          { this.distanceFromCentroid = d; }

    public String getRecommendation()                        { return recommendation; }
    public void   setRecommendation(String r)                { this.recommendation = r; }
}
