package com.smartfest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI configuration.
 *
 * Once the application starts, visit:
 *   http://localhost:8080/swagger-ui.html
 *
 * to see an interactive API explorer where you can test all three APIs
 * directly from your browser — no Postman needed.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI smartFestOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartFest AI Platform API")
                        .description("""
                                Modular AI Intelligence API Platform for Festival Management Systems.
                                
                                Provides three independent AI-powered APIs:
                                - **Recommendation API**: TF-IDF + Cosine Similarity event recommender
                                - **Fraud Detection API**: Rule Engine + K-Means anomaly detector
                                - **RAG Assistant API**: LangChain4j + Google Gemini chatbot
                                
                                OOP Project — Group 17 | BITS Pilani
                                Members: Aryan Pawar | Aditya Goyal | Bharat Nair
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Group 17 — BITS Pilani OOP Project")
                                .email("group17@smartfest.ai"))
                        .license(new License()
                                .name("Academic Use Only")));
    }
}
