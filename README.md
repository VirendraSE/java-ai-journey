# Java AI Journey 🚀

> Built by a **Senior Java Developer** with 15 years of experience
> in Spring Boot and REST APIs, exploring the intersection of
> enterprise Java and modern AI development using Spring AI and Claude.

---

## Tech Stack Badges

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green?style=flat-square&logo=springboot)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M6-blue?style=flat-square)
![Claude](https://img.shields.io/badge/Claude-AI-purple?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

---

## About This Project

A production-ready Spring Boot application demonstrating
AI integration using Spring AI and Anthropic Claude API.
This project showcases how senior Java developers can leverage
their existing expertise to build modern AI-powered applications.

---

## Features

| Feature | Endpoint | Description |
|---|---|---|
| 💬 Stateless Chat | `GET /ask` | One off questions to Claude AI |
| 🧠 Stateful Chat | `POST /chat` | Conversation with memory per user |
| 📡 Streaming | `GET /ask-stream` | Real time word by word responses |
| 📄 Pre-loaded RAG | `GET /ask-pdf` | Questions from pre-loaded PDF |
| 🔄 Dynamic RAG | `POST /ask-my-pdf` | Upload your own PDF and ask questions |

---

## Architecture Highlights

- **SOLID Principles** — Service interface pattern throughout
- **Global Exception Handling** — Consistent error responses
- **Input Validation** — Annotation based validation on all endpoints
- **Proper Logging** — SLF4J with rolling file policy
- **API Documentation** — Swagger UI at `/swagger-ui.html`
- **Session Management** — Per user PDF tracking with HashMap
- **Security** — API keys protected via local properties override

---

## Tech Stack

- **Java 21** — Latest LTS version
- **Spring Boot 3.4.3** — Production grade framework
- **Spring AI 1.0.0-M6** — AI integration framework
- **Anthropic Claude** — claude-sonnet-4-20250514 model
- **SimpleVectorStore** — In memory vector store for RAG
- **SpringDoc 2.8.3** — OpenAPI documentation

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+
- Anthropic API key from console.anthropic.com

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/VirendraSE/java-ai-journey.git
cd java-ai-journey
```

**2. Configure your API key**
```bash
# Create local properties file
cp src/main/resources/application.properties \
   src/main/resources/application-local.properties

# Add your Anthropic API key to application-local.properties
spring.ai.anthropic.api-key=your-api-key-here
```

**3. Run the application**
```bash
mvn spring-boot:run
```

**4. Access Swagger UI**

http://localhost:8080/swagger-ui.html

---

## API Documentation

Full interactive API documentation available at:

http://localhost:8080/swagger-ui.html

---

## Project Structure
```plaintext
java-ai-journey/
├── claude.md
├── vector-store.json
├── src/main/java/com/github/virendra/java_ai_journey/
│   ├── Application.java
│   ├── config/
│   │   └── RagConfig.java
│   ├── controller/
│   │   └── ChatController.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── service/
│   │   ├── VectorStorageService.java
│   │   ├── ChatBoatService.java
│   │   └── impl/
│   │       ├── VectorStorageServiceImpl.java
│   │       └── ClaudeChatBoatServiceImpl.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ChatRequestDTO.java
│   │   │   └── AskMyPdfRequestDTO.java
│   │   └── response/
│   │       ├── ChatResponseDTO.java
│   │       └── ErrorResponseDTO.java
└── src/main/resources/
├── banner.txt
├── docs/
│   └── my-document.pdf
└── application.properties
└── application-local.properties
```
---

## About the Developer

**Virendra** — Senior Java Backend Developer

- 15 years experience in Java, Spring Boot, REST APIs and System Design
- Currently expanding into AI development with Spring AI and Claude
- Building production-ready AI applications using enterprise Java patterns

[![GitHub](https://img.shields.io/badge/GitHub-VirendraSE-black?style=flat-square&logo=github)](https://github.com/VirendraSE)

---

## License

This project is licensed under the MIT License.
