package com.smartfest.controller;

import com.smartfest.model.ApiResponse;
import com.smartfest.service.RagAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller — RAG Assistant API
 *
 * Endpoint:  POST /api/chat
 * Input:     JSON body with a natural language question
 * Output:    A grounded, context-aware answer from Gemini
 *
 * Flow:
 *   1. User asks: "What music events are happening tonight?"
 *   2. Question is converted to an embedding vector
 *   3. Embedding is compared against the festival knowledge base (in-memory vector store)
 *   4. Top-K most relevant festival data chunks are retrieved
 *   5. Chunks + question are sent to Gemini as a grounded prompt
 *   6. Gemini returns a context-aware answer
 *
 * Java features demonstrated:
 *   - Generics: ApiResponse<String>
 *   - LangChain4j integration (AiServices, EmbeddingStore)
 *   - Serialization: knowledge base embeddings cached to disk
 */
@RestController
@RequestMapping("/api")
@Tag(name = "RAG Assistant API", description = "LangChain4j + Google Gemini powered festival assistant")
public class RagAssistantController {

    private final RagAssistantService ragAssistantService;

    public RagAssistantController(RagAssistantService ragAssistantService) {
        this.ragAssistantService = ragAssistantService;
    }

    /**
     * Ask the festival AI assistant a question.
     *
     * Sample request body:
     * {
     *   "question": "Which music events are happening on Day 2?",
     *   "sessionId": "session_abc123"
     * }
     *
     * Sample response:
     * {
     *   "success": true,
     *   "message": "Response generated",
     *   "data": "On Day 2, there are 3 music events: Battle of Bands at 6PM in Main Stage,
     *             Sufi Night at 8PM in Amphitheatre, and DJ Night at 10PM in Club Area.",
     *   "processingTimeMs": 1240
     * }
     */
    @PostMapping("/chat")
    @Operation(summary = "Ask the SmartFest AI assistant a question",
               description = "Uses RAG (Retrieval-Augmented Generation) to answer questions about the festival using a Gemini-powered chatbot grounded in festival data.")
    public ResponseEntity<ApiResponse<String>> chat(
            @RequestBody Map<String, String> request) {

        long startTime = System.currentTimeMillis();

        String question = request.get("question");
        String sessionId = request.getOrDefault("sessionId", "default");

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is required");
        }

        String answer = ragAssistantService.chat(question, sessionId);
        long elapsed = System.currentTimeMillis() - startTime;

        return ResponseEntity.ok(
                ApiResponse.success(answer, "Response generated", elapsed)
        );
    }

    /**
     * Health check for the RAG Assistant API.
     * GET /api/chat/health
     */
    @GetMapping("/chat/health")
    @Operation(summary = "Health check for RAG Assistant API")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("RAG Assistant API is running", "OK", 0)
        );
    }
}
