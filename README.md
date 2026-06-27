# SmartFest AI — Modular AI Intelligence API Platform

> **OOP Project — Group 17 | BITS Pilani**  
> Track 2: AI Algorithms + Advanced Java Programming

---

## 👥 Team

| Name | Role |
|---|---|
| Aryan Pawar | Recommendation API (TF-IDF + Cosine Similarity) |
| Aditya Goyal | Fraud Detection API (Rule Engine + K-Means) |
| Bharat Nair | RAG Assistant API (LangChain4j + Gemini) |

---

## 🎯 What is SmartFest AI?

SmartFest AI is a backend AI platform that plugs into any festival management app and provides three independent AI-powered capabilities:

| API | Endpoint | What it does | Algorithm |
|---|---|---|---|
| **Recommendation API** | `POST /api/recommendations` | Suggests personalised events for each user | TF-IDF + Cosine Similarity |
| **Fraud Detection API** | `POST /api/fraud-check` | Flags suspicious wallet transactions | Rule Engine + K-Means Clustering |
| **RAG Assistant API** | `POST /api/chat` | Answers participant questions about the fest | LangChain4j + Google Gemini |

---

## 🏗️ Project Architecture

```
smartfest-ai/
├── src/main/java/com/smartfest/
│   ├── SmartFestApplication.java       ← Main entry point
│   ├── controller/
│   │   ├── RecommendationController.java  ← POST /api/recommendations
│   │   ├── FraudDetectionController.java  ← POST /api/fraud-check
│   │   └── RagAssistantController.java    ← POST /api/chat
│   ├── service/
│   │   ├── RecommendationService.java     ← TF-IDF + Cosine Similarity logic
│   │   ├── FraudDetectionService.java     ← Rule Engine + K-Means logic
│   │   └── RagAssistantService.java       ← LangChain4j + Gemini integration
│   ├── model/
│   │   ├── Event.java                     ← Festival event data
│   │   ├── User.java                      ← Participant profile
│   │   ├── Transaction.java               ← Wallet transaction
│   │   ├── EventScore.java                ← Event + similarity score pair
│   │   ├── FraudResult.java               ← Fraud detection output
│   │   └── ApiResponse.java               ← Generic<T> response wrapper
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java    ← @ControllerAdvice error handling
│   │   ├── UserNotFoundException.java
│   │   └── EventNotFoundException.java
│   └── config/
│       └── SwaggerConfig.java             ← OpenAPI/Swagger UI config
├── src/main/resources/
│   ├── application.properties
│   └── data/
│       ├── events.csv                     ← Festival event dataset (90+ events)
│       ├── users.json                     ← Sample user profiles (coming soon)
│       └── transactions.json              ← Sample transactions (coming soon)
└── src/test/
    └── SmartFestApplicationTests.java     ← JUnit 5 unit tests
```

---

## ⚙️ Java Features Used (11 Required)

| # | Feature | Where Used |
|---|---|---|
| 1 | **Java Collections** (`HashMap`, `ArrayList`, `List`) | TF-IDF index, event store, observer list |
| 2 | **Generics** (`List<T>`, `Map<K,V>`, `ApiResponse<T>`) | All models and service return types |
| 3 | **Streams API** (`.stream().map().sorted().collect()`) | Top-N event ranking pipeline |
| 4 | **Lambda Expressions** | Comparator, forEach, filter predicates |
| 5 | **File I/O** (`BufferedReader`, `FileReader`) | Loading events.csv and datasets |
| 6 | **Custom Exceptions** (extends `RuntimeException`) | `UserNotFoundException`, `EventNotFoundException` |
| 7 | **Multithreading** (`ExecutorService`, thread pool) | Parallel fraud scoring across transactions |
| 8 | **Concurrency** (`ConcurrentHashMap`) | Thread-safe cluster assignment cache |
| 9 | **Serialization** (`ObjectOutputStream`) | Caching TF-IDF vectors to disk |
| 10 | **Observer Pattern** | `FraudAlertObserver` notified on fraud detection |
| 11 | **Factory Pattern** | `ApiServiceFactory` for service selection |

---

## 🤖 AI Algorithms Implemented

### 1. TF-IDF + Cosine Similarity (Recommendation API)
- **TF-IDF**: Converts event descriptions into numerical vectors that represent the importance of each word
- **Cosine Similarity**: Measures the angle between a user's preference vector and each event's vector
- **Result**: Events closest in vector space to the user's interests are recommended

### 2. Rule Engine + K-Means Clustering (Fraud Detection API)
- **Rule Engine**: Fast deterministic checks (amount spike, rapid transactions, velocity patterns)
- **K-Means**: Groups historical transactions into clusters; outliers far from any centroid are anomalous
- **Combined Score**: `0.6 × rule_score + 0.4 × kmeans_anomaly_score` → final risk score [0.0–1.0]

### 3. RAG (Retrieval-Augmented Generation) — Assistant API
- **Ingestion**: Festival data chunked and embedded using Gemini Embedding model
- **Retrieval**: User question embedded → similarity search → top-K relevant chunks retrieved
- **Generation**: Retrieved context + question sent to Gemini → grounded, accurate answer

---

## 🚀 How to Run

### Prerequisites
- Java 21+
- Maven 3.8+
- IntelliJ IDEA (recommended)

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/smartfest-ai.git
cd smartfest-ai
```

### 2. Set your Gemini API key
```bash
export GEMINI_API_KEY=your_gemini_api_key_here
# Get a free key at: https://aistudio.google.com
```

### 3. Build and run
```bash
mvn spring-boot:run
```

### 4. Open Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 5. Test the APIs (examples)

**Recommendation API:**
```bash
curl -X POST http://localhost:8080/api/recommendations \
  -H "Content-Type: application/json" \
  -d '{"userId": "u_001", "topN": 5}'
```

**Fraud Detection API:**
```bash
curl -X POST http://localhost:8080/api/fraud-check \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"txn_001","userId":"u_001","amount":50000,"type":"REGISTRATION","timestamp":"2025-11-14T23:00:00","ipAddress":"192.168.1.1","deviceId":"dev_001"}'
```

**RAG Assistant API:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "What music events are happening on Day 1?", "sessionId": "s_001"}'
```

### 6. Run unit tests
```bash
mvn test
```

---

## 📋 Implementation Status

- [x] Project structure and Maven setup
- [x] All 3 REST controllers with Swagger documentation
- [x] Data models: Event, User, Transaction, FraudResult, EventScore, ApiResponse<T>
- [x] Custom exceptions + GlobalExceptionHandler
- [x] Observer pattern skeleton (FraudAlertObserver)
- [x] Fraud rule engine (basic rules)
- [x] JUnit 5 test suite (fraud detection tests)
- [x] Sample events dataset (events.csv)
- [ ] TF-IDF engine implementation
- [ ] Cosine similarity computation + ranking
- [ ] K-Means clustering implementation
- [ ] Multithreaded fraud scoring (ExecutorService)
- [ ] LangChain4j + Gemini RAG pipeline
- [ ] Full dataset (users.json, transactions.json)
- [ ] Serialization of TF-IDF index to disk
- [ ] Factory pattern implementation
- [ ] Complete unit test suite (20+ tests)

---

## 🔗 References
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [LangChain4j Documentation](https://docs.langchain4j.dev)
- [Google Gemini API](https://aistudio.google.com)
- [Baeldung — Java Reference](https://www.baeldung.com)
