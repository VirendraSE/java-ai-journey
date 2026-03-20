package com.github.virendra.java_ai_journey.controller;

import com.github.virendra.java_ai_journey.dto.request.ChatRequestDTO;
import com.github.virendra.java_ai_journey.dto.response.ChatResponseDTO;
import com.github.virendra.java_ai_journey.service.ChatBoatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai-journey/chatbot")
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
        String message = "Hello, this application is up and running!!";
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
    public ResponseEntity<ChatResponseDTO> askYourQuestion(@RequestParam String question) {
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
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO chatRequest) {
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
     * @param chatRequestDTO body
     * @return answer from Claude based on PDF content
     */
    @GetMapping("/ask-pdf")
    public ResponseEntity<ChatResponseDTO> askPdf(@RequestBody ChatRequestDTO chatRequestDTO){
        String answer = chatBoatService.askPdf(chatRequestDTO.getQuestion());
        ChatResponseDTO chatResponseDTO = ChatResponseDTO.builder()
                .success(true)
                .answer(answer)
                .build();

        return ResponseEntity.ok(chatResponseDTO);
    }
}
