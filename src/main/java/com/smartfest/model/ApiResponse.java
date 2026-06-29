package com.smartfest.model;

/**
 * Generic API response wrapper.
 *
 * Used by ALL three APIs to ensure consistent JSON response structure.
 * The <T> type parameter (Generics) lets this class wrap any response type:
 *
 *   ApiResponse<List<EventScore>>    for Recommendation API
 *   ApiResponse<FraudResult>         for Fraud Detection API
 *   ApiResponse<String>              for RAG Assistant API
 *
 * Example JSON output:
 * {
 *   "success": true,
 *   "message": "Recommendations generated successfully",
 *   "data": [ ... ],
 *   "processingTimeMs": 42
 * }
 *
 * Java feature used: Generics (<T>)
 */
public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;
    private long    processingTimeMs;
    private String  apiVersion = "1.0";

    // ── Static factory methods (clean builder-style creation) ─────

    public static <T> ApiResponse<T> success(T data, String message, long processingTimeMs) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success           = true;
        response.message           = message;
        response.data              = data;
        response.processingTimeMs  = processingTimeMs;
        response.apiVersion        = "1.0";
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success    = false;
        response.message    = message;
        response.data       = null;
        response.apiVersion = "1.0";
        return response;
    }

    // ── Getters & Setters ─────────────────────────────────────────

    public boolean isSuccess()                          { return success; }
    public void    setSuccess(boolean success)          { this.success = success; }

    public String  getMessage()                         { return message; }
    public void    setMessage(String message)           { this.message = message; }

    public T       getData()                            { return data; }
    public void    setData(T data)                      { this.data = data; }

    public long    getProcessingTimeMs()                { return processingTimeMs; }
    public void    setProcessingTimeMs(long ms)         { this.processingTimeMs = ms; }

    public String  getApiVersion()                      { return apiVersion; }
    public void    setApiVersion(String v)              { this.apiVersion = v; }
}
