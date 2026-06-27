package com.smartfest.controller;

import com.smartfest.model.ApiResponse;
import com.smartfest.model.EventScore;
import com.smartfest.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller — Recommendation API
 *
 * Endpoint:  POST /api/recommendations
 * Input:     JSON body with userId and optional topN count
 * Output:    JSON list of top-N events ranked by cosine similarity score
 *
 * Java features demonstrated:
 *   - @RestController (Spring annotation-based DI)
 *   - Generics:  ApiResponse<List<EventScore>>
 *   - Collections: returned as List<EventScore>
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Recommendation API", description = "TF-IDF + Cosine Similarity event recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // Constructor injection — Spring automatically wires the service
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Get personalised event recommendations for a user.
     *
     * Sample request body:
     * {
     *   "userId": "u_001",
     *   "topN": 5
     * }
     *
     * Sample response:
     * {
     *   "success": true,
     *   "message": "Recommendations generated successfully",
     *   "data": [
     *     { "event": { "eventId": "e_042", "title": "Battle of Bands", ... }, "score": 0.8732 },
     *     ...
     *   ],
     *   "processingTimeMs": 38
     * }
     */
    @PostMapping("/recommendations")
    @Operation(summary = "Get top-N event recommendations for a user",
               description = "Uses TF-IDF vectorisation and cosine similarity to rank events based on user's browsing history and preferences.")
    public ResponseEntity<ApiResponse<List<EventScore>>> getRecommendations(
            @RequestBody Map<String, Object> request) {

        long startTime = System.currentTimeMillis();

        String userId = (String) request.get("userId");
        int topN = request.containsKey("topN")
                ? (Integer) request.get("topN")
                : 5;

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }

        List<EventScore> recommendations = recommendationService.recommend(userId, topN);
        long elapsed = System.currentTimeMillis() - startTime;

        return ResponseEntity.ok(
                ApiResponse.success(recommendations,
                        "Recommendations generated successfully (" + recommendations.size() + " events)",
                        elapsed)
        );
    }

    /**
     * Health check for the Recommendation API.
     * GET /api/recommendations/health
     */
    @GetMapping("/recommendations/health")
    @Operation(summary = "Health check for Recommendation API")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("Recommendation API is running", "OK", 0)
        );
    }
}
