# Java AI Journey 🚀

A Spring Boot application demonstrating AI integration using
Spring AI and Anthropic Claude API.

## Features
- 💬 Stateless chat with Claude AI
- 🧠 Stateful conversation with memory per user
- 📡 Real time streaming responses with Flux
- 📄 RAG (Retrieval Augmented Generation) with PDF documents

## Tech Stack
- Java 21
- Spring Boot 3.4.3
- Spring AI 1.0.0-M6
- Anthropic Claude API (claude-sonnet-4-20250514)
- Maven

## My Project Structure
java-ai-journey/
├── claude.md
├── vector-store.json
├── src/main/java/com/github/virendra/java_ai_journey/
│   ├── Application.java
│   ├── config/
│   │   └── RagConfig.java
│   ├── controller/
│   │   └── ChatController.java
│   ├── service/
│   │   ├── impl/
│   │   │   └── ClaudeChatBoatServiceImpl.java
│   │   └── ChatBoatService.java
│   ├── dto/
│   │   ├── request/
│   │   │   └── ChatRequestDTO.java
│   │   └── response/
│   │       └── ChatResponseDTO.java
└── src/main/resources/
├── docs/
│   └── my-document.pdf
└── application.properties
└── application_local.properties

## Endpoints
| Endpoint                          | Method | Description |
| `/ai-journey/chatbot/isAlive`     | GET    | Health check |
| `/ai-journey/chatbot/ask`         | GET    | Stateless question |
| `/ai-journey/chatbot/chat`        | POST   | Stateful conversation |
| `/ai-journey/chatbot/ask-stream`  | GET    | Streaming response |
| `/ai-journey/chatbot/ask-pdf`     | GET    | RAG question from PDF |

## Setup
1. Clone the repository : git clone https://github.com/VirendraSE/java-ai-journey.git
2. Open `application.properties`
3. Replace 'spring.ai.anthropic.api-key' property with your Anthropic API key
4. Run `mvn spring-boot:run`

## Author
**Virendra** — Senior Java Backend Developer  
15 years experience in Java, Spring Boot, REST APIs and System Design  
Currently exploring AI development with Spring AI.

## License
MIT License