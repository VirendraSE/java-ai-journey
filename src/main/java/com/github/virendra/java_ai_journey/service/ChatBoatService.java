package com.github.virendra.java_ai_journey.service;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

/**
 * Service interface for AI chat operations.
 * Defines contract for stateless, stateful,
 * streaming and RAG based chat capabilities.
 *
 * @author Virendra
 * @version 1.0.0
 */
public interface ChatBoatService {

   String ask(String question);
    Flux<String> askStream(String question);
    String chat(String question,  String userID);

    String askPdf(String question);

    /**
     * Dynamic RAG - answers from user uploaded PDF document
     */
    String askMyPdf(
            MultipartFile file,
            String sessionId,
            String question);
}
