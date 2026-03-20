package com.github.virendra.java_ai_journey.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface ChatBoatService {

    public void loadPdfToVectorStore();

    public String ask(String question);
    public Flux<String> askStram(String question);
    public  String chat(String question,  String userID);

    public String askPdf(String question);
}
