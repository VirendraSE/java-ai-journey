package com.github.virendra.java_ai_journey.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.virendra.java_ai_journey.dto.request.ChatRequestDTO;
import com.github.virendra.java_ai_journey.dto.response.ChatResponseDTO;
import com.github.virendra.java_ai_journey.service.ChatBoatService;
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

import java.io.IOException;

@RestController
@RequestMapping("/ai-journey/chatbot")
@Validated // This tells Spring to validate @RequestParam fields too!
public class ChatController {

    ChatBoatService chatBoatService;

    public ChatController(ChatBoatService chatBoatService) {
        this.chatBoatService = chatBoatService;
    }

    /**
     *  This function use for check Application status.
     * @return a standard message if application is keep and running successfully.
     */
    @GetMapping("/isAlive")
    public ResponseEntity<String> isAlive() {
        String message = "Up and running!!";
        ResponseEntity<String> response = new ResponseEntity<>(message, HttpStatus.OK);
        return response;
    }

    /**
     * Stateless endpoint - no memory
     * @param question
     * @return the response from Claude AI.
     * This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
     */
    @GetMapping("/ask")
    public ResponseEntity<ChatResponseDTO> askYourQuestion(
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
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO chatRequest) {
        String answer = chatBoatService.chat(chatRequest.getQuestion(), chatRequest.getUserName());
        ChatResponseDTO chatResponse = ChatResponseDTO.builder()
                .success(true)
                .answer(answer)
                .build();

        return ResponseEntity.ok(chatResponse);      // 200 OK
    }

    // New streaming endpoint - returns word by word in real time
    @GetMapping(value = "/ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askWithStreaming(@RequestParam String question) {
        return chatBoatService.askStram(question);
    }

    /**
     * RAG endpoint - answers questions from PDF document
     * @param question
     * @return answer from Claude based on PDF content
     */
    @GetMapping("/ask-pdf")
    public ResponseEntity<ChatResponseDTO> askPdf(
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

    @PostMapping(value = "/ask-my-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatResponseDTO> askMyPdf(@NotNull(message = "Please upload a PDF file") @RequestParam("file") MultipartFile file,
                                                    @NotBlank(message = "Session ID cannot be empty") @Size(min = 2, max = 50, message = "Session ID must be between 2 and 50 characters")
                                                    @RequestParam("sessionId") String sessionId,
                                                    @NotBlank(message = "Question cannot be empty") @Size(min = 2, max = 100, message = "Question must be between 2 and 100 characters")
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
