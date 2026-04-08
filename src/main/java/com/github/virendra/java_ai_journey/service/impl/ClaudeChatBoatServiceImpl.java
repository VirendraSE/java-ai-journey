package com.github.virendra.java_ai_journey.service.impl;

import com.github.virendra.java_ai_journey.service.ChatBoatService;
import com.github.virendra.java_ai_journey.service.VectorStorageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
@Service
public class ClaudeChatBoatServiceImpl implements ChatBoatService {

    private final ChatClient chatClient;
    private final int inChatHistoryWindowSize = 10;
    private final VectorStorageService vectorStorageService;
    private final VectorStore vectorStore;

    private final InMemoryChatMemory inMemoryChatMemory = new InMemoryChatMemory();

    public ClaudeChatBoatServiceImpl(ChatClient.Builder builder, VectorStorageService vectorStorageService, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStorageService = vectorStorageService;
        this.vectorStore = vectorStore;
    }

    @Override
    public String ask(String question) {
        String response = null;

        log.info("START: Calling AI APIs to ask a question.");
        // This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
        response = chatClient
                    .prompt()
                    .user(question)
                    .call()
                    .content();
        log.info("END: received response from AI APIs for a question.");
        return response;
    }

    @Override
    public Flux<String> askStream(String question) {
        return chatClient
                .prompt()
                .user(question)
                .stream()      // ← non-blocking - streams as generated
                .content();
    }

    /**
     * Stateful endpoint - remembers conversation per user
     * @param question the question to ask Claude
     * @param userID unique identifier for conversation session
     * @return answer from Claude with conversation context
     */
    @Override
    public String chat(String question, String userID) {
        String response =  null;

        // This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
        log.info("START: calling AI APIs with sessionID : {}." , userID);
        response = chatClient
                    .prompt()
                    .user(question)
                    // Builder pattern with updated memory class
                    .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, userID, inChatHistoryWindowSize))
                    .call()
                    .content();
        log.info("End: received response from AI APIs with sessionID : {}." , userID);
        return response;
    }

    /**
     * RAG endpoint - answers questions from YOUR PDF document
     * @param question the question to ask about the PDF
     * @return answer from Claude based on PDF content
     */
    @Override
    public String askPdf(String question) {
        String response =  null;
        log.info("START: calling AI APIs for pre-loaded PDF doc.");
        response = chatClient
                    .prompt()
                    .user(question)
                    .advisors(new QuestionAnswerAdvisor(vectorStore))
                    .call()
                    .content();
        log.info("END: received response from AI APIs for pre-loaded PDF doc.");
        return response;
    }

    /**
     * Dynamic RAG - answers from user uploaded PDF per session
     * @param file      uploaded PDF file
     * @param sessionId unique session identifier
     * @param question  the question to ask about PDF
     * @return answer from Claude based on uploaded PDF
     */
    @Override
    public String askMyPdf(MultipartFile file, String sessionId, String question)  {
        String response = null;
        // Step 1 - Load PDF for this session
        // Skips automatically if PDF is already loaded!
        try {
            log.info("START: loading PDF doc given by user");
            vectorStorageService.loadUserPdfToVectorStore(file, sessionId);
            log.info("End: loaded PDF doc given by user");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("START: calling AI APIs for given PDF doc.");
            response = chatClient
                    .prompt()
                    .user(question)
                    .advisors(new QuestionAnswerAdvisor(vectorStore))
                    .call()
                    .content();
        log.info("End: received response from AI APIs for given PDF doc.");

        return response;
    }
}
