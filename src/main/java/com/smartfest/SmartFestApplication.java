package com.smartfest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SmartFest AI Platform — Main Entry Point
 *
 * A modular AI Intelligence API Platform for Festival Management Systems.
 * Exposes three independent REST APIs:
 *
 *   POST /api/recommendations  → TF-IDF + Cosine Similarity based event recommender
 *   POST /api/fraud-check      → Rule Engine + K-Means Clustering fraud detector
 *   POST /api/chat             → RAG-powered festival assistant (LangChain4j + Gemini)
 *
 * Swagger UI available at: http://localhost:8080/swagger-ui.html
 *
 * OOP Project — Group 17
 * Members: Aryan Pawar | Aditya Goyal | Bharat Nair
 * Track 2: AI Algorithms + Advanced Java Programming
 */
@SpringBootApplication
public class SmartFestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFestApplication.class, args);
    }
}
