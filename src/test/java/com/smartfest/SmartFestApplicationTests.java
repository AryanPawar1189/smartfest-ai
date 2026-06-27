package com.smartfest;

import com.smartfest.model.FraudResult;
import com.smartfest.model.Transaction;
import com.smartfest.service.FraudDetectionService;
import com.smartfest.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmartFest AI services.
 *
 * Java feature used: JUnit 5 (@Test, @DisplayName, @SpringBootTest)
 *
 * Run with: mvn test
 * Or: Right-click → Run in IntelliJ IDEA
 */
@SpringBootTest
class SmartFestApplicationTests {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private RecommendationService recommendationService;

    // ── Application Context Test ───────────────────────────────────

    @Test
    @DisplayName("Spring Boot application context loads successfully")
    void contextLoads() {
        // This test passes if the Spring Boot app starts without errors
        assertNotNull(fraudDetectionService);
        assertNotNull(recommendationService);
    }

    // ── Fraud Detection Tests ──────────────────────────────────────

    @Test
    @DisplayName("High-amount transaction should be flagged as fraud")
    void testFraudDetection_HighAmount_ShouldFlag() {
        Transaction txn = new Transaction(
                "txn_test_001", "u_001", 50000.0,
                Transaction.Type.REGISTRATION,
                "2025-11-14T22:00:00", "192.168.1.1", "dev_001"
        );

        FraudResult result = fraudDetectionService.check(txn);

        assertTrue(result.isFraud(), "Transaction with amount 50,000 should be flagged");
        assertTrue(result.getTriggeredRules().contains("amount_spike"),
                "Rule 'amount_spike' should be triggered");
        assertEquals("BLOCK", result.getRecommendation(),
                "High risk transaction should be blocked");
    }

    @Test
    @DisplayName("Normal transaction should not be flagged")
    void testFraudDetection_NormalAmount_ShouldPass() {
        Transaction txn = new Transaction(
                "txn_test_002", "u_002", 500.0,
                Transaction.Type.FOOD,
                "2025-11-14T12:30:00", "192.168.1.2", "dev_002"
        );

        FraudResult result = fraudDetectionService.check(txn);

        assertFalse(result.isFraud(), "Normal transaction of 500 should not be flagged");
        assertEquals("APPROVE", result.getRecommendation());
    }

    @Test
    @DisplayName("Zero amount transaction should trigger invalid_amount rule")
    void testFraudDetection_ZeroAmount_ShouldTriggerRule() {
        Transaction txn = new Transaction(
                "txn_test_003", "u_003", 0.0,
                Transaction.Type.FOOD,
                "2025-11-14T13:00:00", "10.0.0.1", "dev_003"
        );

        FraudResult result = fraudDetectionService.check(txn);

        assertTrue(result.getTriggeredRules().contains("invalid_amount"));
    }

    @Test
    @DisplayName("FraudResult risk score should be between 0.0 and 1.0")
    void testFraudResult_RiskScore_ShouldBeNormalized() {
        Transaction txn = new Transaction(
                "txn_test_004", "u_004", 999999.0,
                Transaction.Type.WALLET_TOPUP,
                "2025-11-14T03:00:00", "1.2.3.4", "dev_004"
        );

        FraudResult result = fraudDetectionService.check(txn);

        assertTrue(result.getRiskScore() >= 0.0 && result.getRiskScore() <= 1.0,
                "Risk score must be between 0.0 and 1.0");
    }

    // ── TODO: Add tests as implementation progresses ───────────────
    // @Test testRecommendation_ValidUser_ReturnsFiveEvents()
    // @Test testRecommendation_UserWithNoHistory_ReturnsPopularEvents()
    // @Test testCosineSimilarity_IdenticalVectors_ReturnsOne()
    // @Test testCosineSimilarity_OrthogonalVectors_ReturnsZero()
}
