package com.smartfest.model;

/**
 * Represents a wallet transaction at the festival.
 *
 * Used by the Fraud Detection API.
 * Each transaction is converted into a feature vector [amount, frequency, timeSinceLast]
 * for K-Means clustering and anomaly scoring.
 *
 * Java features used: Serialization (implements Serializable for caching)
 */
public class Transaction {

    public enum Type { REGISTRATION, FOOD, MERCHANDISE, WALLET_TOPUP, TRANSFER }

    private String transactionId;
    private String userId;
    private double amount;
    private Type   type;
    private String timestamp;           // ISO-8601 format: "2025-11-14T22:13:45"
    private String ipAddress;
    private String deviceId;
    private String eventId;             // null for non-event transactions (food, merch)
    private boolean flaggedByRules;     // set by RuleEngine
    private double riskScore;           // 0.0 (safe) to 1.0 (definitely fraud)

    // ── Constructors ──────────────────────────────────────────────

    public Transaction() {}

    public Transaction(String transactionId, String userId,
                       double amount, Type type, String timestamp,
                       String ipAddress, String deviceId) {
        this.transactionId  = transactionId;
        this.userId         = userId;
        this.amount         = amount;
        this.type           = type;
        this.timestamp      = timestamp;
        this.ipAddress      = ipAddress;
        this.deviceId       = deviceId;
        this.riskScore      = 0.0;
        this.flaggedByRules = false;
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public String getTransactionId()                      { return transactionId; }
    public void   setTransactionId(String id)             { this.transactionId = id; }

    public String getUserId()                             { return userId; }
    public void   setUserId(String userId)                { this.userId = userId; }

    public double getAmount()                             { return amount; }
    public void   setAmount(double amount)                { this.amount = amount; }

    public Type   getType()                               { return type; }
    public void   setType(Type type)                      { this.type = type; }

    public String getTimestamp()                          { return timestamp; }
    public void   setTimestamp(String timestamp)          { this.timestamp = timestamp; }

    public String getIpAddress()                          { return ipAddress; }
    public void   setIpAddress(String ip)                 { this.ipAddress = ip; }

    public String getDeviceId()                           { return deviceId; }
    public void   setDeviceId(String deviceId)            { this.deviceId = deviceId; }

    public String getEventId()                            { return eventId; }
    public void   setEventId(String eventId)              { this.eventId = eventId; }

    public boolean isFlaggedByRules()                     { return flaggedByRules; }
    public void   setFlaggedByRules(boolean f)            { this.flaggedByRules = f; }

    public double getRiskScore()                          { return riskScore; }
    public void   setRiskScore(double riskScore)          { this.riskScore = riskScore; }

    @Override
    public String toString() {
        return "Transaction{id='" + transactionId + "', userId='" + userId +
               "', amount=" + amount + ", type=" + type + "}";
    }
}
