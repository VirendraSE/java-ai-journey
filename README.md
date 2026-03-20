# Java AI Journey

A Spring Boot application demonstrating AI integration using Spring AI and Claude.

## Features
- Stateless chat with Claude AI
- Stateful conversation with memory per user
- Real time streaming responses
- RAG (Retrieval Augmented Generation) with PDF documents

## Tech Stack
- Java 21
- Spring Boot 3.4.3
- Spring AI 1.0.0-M6
- Anthropic Claude API
- Maven

## Endpoints
| Endpoint | Method | Description |
|---|---|---|
| `/ai-journey/chatbot/isAlive` | GET | Health check |
| `/ai-journey/chatbot/ask` | GET | Stateless question |
| `/ai-journey/chatbot/chat` | POST | Stateful conversation |
| `/ai-journey/chatbot/ask-stream` | GET | Streaming response |
| `/ai-journey/chatbot/ask-pdf` | GET | RAG question from PDF |

## Setup
1. Clone the repository
2. Open `application.properties`
3. Replace 'spring.ai.anthropic.api-key' property with your Anthropic API key
4. Run `mvn spring-boot:run`

## Author
Virendra — Java Developer
exploring AI development with Spring AI