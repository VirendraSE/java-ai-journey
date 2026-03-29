package com.github.virendra.java_ai_journey.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface ChatBoatService {

    public String ask(String question);
    public Flux<String> askStram(String question);
    public  String chat(String question,  String userID);

    public String askPdf(String question);

    /**
     * Dynamic RAG - answers from user uploaded PDF document
     */
    String askMyPdf(
            MultipartFile file,
            String sessionId,
            String question) throws IOException;
}
