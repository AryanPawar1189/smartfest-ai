package com.smartfest.controller;

import com.smartfest.model.ApiResponse;
import com.smartfest.model.FraudResult;
import com.smartfest.model.Transaction;
import com.smartfest.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller — Fraud Detection API
 *
 * Endpoint:  POST /api/fraud-check
 * Input:     JSON body representing a Transaction
 * Output:    FraudResult with risk score, triggered rules, and recommendation
 *
 * Java features demonstrated:
 *   - Generics: ApiResponse<FraudResult>
 *   - Custom Exceptions (thrown by service, handled by GlobalExceptionHandler)
 *   - Multithreading: fraud scoring runs on a thread pool inside the service
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Fraud Detection API", description = "Rule Engine + K-Means Clustering anomaly detection")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    public FraudDetectionController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    /**
     * Check a transaction for fraud.
     *
     * Sample request body:
     * {
     *   "transactionId": "txn_891",
     *   "userId": "u_045",
     *   "amount": 45000,
     *   "type": "REGISTRATION",
     *   "timestamp": "2025-11-14T23:45:00",
     *   "ipAddress": "192.168.1.100",
     *   "deviceId": "dev_xyz"
     * }
     *
     * Sample response:
     * {
     *   "success": true,
     *   "data": {
     *     "transactionId": "txn_891",
     *     "fraud": true,
     *     "riskScore": 0.87,
     *     "triggeredRules": ["amount_spike", "abnormal_pattern"],
     *     "assignedCluster": 2,
     *     "distanceFromCentroid": 4.32,
     *     "recommendation": "BLOCK"
     *   }
     * }
     */
    @PostMapping("/fraud-check")
    @Operation(summary = "Check a transaction for fraudulent activity",
               description = "Runs a rule engine check followed by K-Means clustering anomaly detection. Returns a risk score from 0.0 (safe) to 1.0 (fraud).")
    public ResponseEntity<ApiResponse<FraudResult>> checkFraud(
            @RequestBody Transaction transaction) {

        long startTime = System.currentTimeMillis();

        if (transaction.getTransactionId() == null || transaction.getUserId() == null) {
            throw new IllegalArgumentException("transactionId and userId are required");
        }

        FraudResult result = fraudDetectionService.check(transaction);
        long elapsed = System.currentTimeMillis() - startTime;

        return ResponseEntity.ok(
                ApiResponse.success(result,
                        "Fraud check completed — " + result.getRecommendation(),
                        elapsed)
        );
    }

    /**
     * Health check for the Fraud Detection API.
     * GET /api/fraud-check/health
     */
    @GetMapping("/fraud-check/health")
    @Operation(summary = "Health check for Fraud Detection API")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("Fraud Detection API is running", "OK", 0)
        );
    }
}
