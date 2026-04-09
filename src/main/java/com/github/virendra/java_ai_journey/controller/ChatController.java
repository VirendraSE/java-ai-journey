package com.github.virendra.java_ai_journey.controller;

import com.github.virendra.java_ai_journey.dto.request.ChatRequestDTO;
import com.github.virendra.java_ai_journey.dto.response.ChatResponseDTO;
import com.github.virendra.java_ai_journey.service.ChatBoatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

/**
 * REST Controller for AI chatbot endpoints.
 * Provides stateless, stateful, streaming
 * and RAG based chat capabilities.
 *
 * @author Virendra
 * @version 1.0.0
 */
@RestController
@RequestMapping("/ai-journey/chatbot")
@Validated // This tells Spring to validate @RequestParam fields too!
@Tag(
        name = "Chatbot API", description = "Spring boot AI powered Chat-bot endpoints with Retrieval-Augmented Generation (RAG) support"
    ) //Groups all endpoints under one tag in Swagger UI
public class ChatController {

    final ChatBoatService chatBoatService;

    public ChatController(ChatBoatService chatBoatService) {
        this.chatBoatService = chatBoatService;
    }

    /**
     *  This function use for check Application status.
     * @return a standard message if application is keep and running successfully.
     */
    @Operation(
            summary = "Health check",
            description = "Check if the application is Up and running")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Application is Up and running")})
    @GetMapping("/isAlive")
    public ResponseEntity<String> isAlive() {
        String message = "Up and running!!";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Stateless endpoint - no memory
     * @param question
     * @return the response from Claude AI.
     * This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
     */
    @Operation(
            summary = "Stateless chat",
            description = "Ask Claude a question without any conversation memory")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Answer retrieved successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/ask")
    public ResponseEntity<ChatResponseDTO> askYourQuestion(
            @Parameter(description = "Question to ask Claude", example = "What is RAG, explain it in two sentences?")
            @NotBlank(message = "Question cannot be empty")
            @Size(min = 2, max = 100, message = "Question must be between 2 and 100 characters")
            @RequestParam String question) {
        String answer = chatBoatService.ask(question);
        ChatResponseDTO chatResponse = ChatResponseDTO.builder()
                .success(true)
                .answer(answer)
                .build();
        return ResponseEntity.ok(chatResponse);       // 200 OK
    }

    /**
     * Stateful endpoint - remembers conversation per user
     * @param chatRequest body
     * @return the response from Claude AI.
     * This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
     */
    @Operation(
            summary = "Stateful chat with memory",
            description = "Chat with Claude with conversation memory per user session")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Answer retrieved successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Validation error"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponseDTO> chat(
            @Valid @RequestBody ChatRequestDTO chatRequest) {
        String answer = chatBoatService.chat(chatRequest.getQuestion(), chatRequest.getUserName());
        ChatResponseDTO chatResponse = ChatResponseDTO.builder()
                .success(true)
                .answer(answer)
                .build();

        return ResponseEntity.ok(chatResponse);      // 200 OK
    }

    // New streaming endpoint - returns word by word in real time
    @Operation(
            summary = "Streaming chat",
            description = "Ask Claude a question and receive response word by word in real time")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Streaming response started"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input")
    })
    @GetMapping(value = "/ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askWithStreaming(
            @Parameter(description = "Question to ask Claude",
                    example = "Tell me about Spring Boot")
            @NotBlank(message = "Question cannot be empty")
            @Size(min = 2, max = 100,
                    message = "Question must be between 2 and 100 characters")
            @RequestParam String question) {
        return chatBoatService.askStream(question);
    }

    /**
     * RAG endpoint - answers questions from PDF document
     * @param question
     * @return answer from Claude based on PDF content
     */
    @Operation(
            summary = "Ask from pre-loaded PDF",
            description = "Ask Claude questions about the pre-loaded PDF document using RAG")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Answer retrieved from PDF successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/ask-pdf")
    public ResponseEntity<ChatResponseDTO> askPdf(
            @Parameter(description = "Question to ask about the pre-loaded PDF",
                    example = "What is this document about?")
            @NotBlank(message = "Question cannot be empty")
            @Size(min = 2, max = 100, message = "Question must be between 2 and 100 characters")
            @RequestParam String question
    ){
        String answer = chatBoatService.askPdf(question);
        ChatResponseDTO chatResponseDTO = ChatResponseDTO.builder()
                .success(true)
                .answer(answer)
                .build();

        return ResponseEntity.ok(chatResponseDTO);
    }

    @Operation(
            summary = "Dynamic PDF upload with RAG",
            description = "Upload your own PDF and ask questions about it using session based RAG")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Answer retrieved from uploaded PDF"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid file or input"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping(value = "/ask-my-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatResponseDTO> askMyPdf(
            @Parameter(description = "PDF file to upload and query")
            @NotNull(message = "Please upload a PDF file")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Unique session identifier",
                    example = "virendra-session-1")
            @NotBlank(message = "Session ID cannot be empty")
            @Size(min = 2, max = 50, message = "Session ID must be between 2 and 50 characters")
            @RequestParam("sessionId") String sessionId,
            @Parameter(description = "Question about the PDF",
                    example = "What is this document about?")
            @NotBlank(message = "Question cannot be empty")
            @Size(min = 2, max = 100, message = "Question must be between 2 and 100 characters")
            @RequestParam("question") String question) {
        String answer = null;
        ChatResponseDTO chatResponseDTO;
            answer = chatBoatService.askMyPdf(file, sessionId, question);
            chatResponseDTO = ChatResponseDTO.builder()
                    .success(true)
                    .answer(answer)
                    .build();

        return ResponseEntity.ok(chatResponseDTO);
    }
}
