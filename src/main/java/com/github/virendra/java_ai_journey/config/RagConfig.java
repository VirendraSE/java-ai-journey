package com.github.virendra.java_ai_journey.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {

    /**
     * Creates a SimpleVectorStore bean that stores
     * document embeddings in memory.
     * EmbeddingModel is automatically injected by Spring AI.
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel){
        // Note: SimpleVectorStore stores everything in RAM
        return SimpleVectorStore
                .builder(embeddingModel)
                .build();
    }
}
