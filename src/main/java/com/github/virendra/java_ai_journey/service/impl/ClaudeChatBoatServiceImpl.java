package com.github.virendra.java_ai_journey.service.impl;

import com.github.virendra.java_ai_journey.service.ChatBoatService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@Component("ClaudeBean")
public class ClaudeChatBoatServiceImpl implements ChatBoatService {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeChatBoatServiceImpl.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final int inChatHistoryWindowSize = 10;

    // Reads the pdf path from application.properties
    @Value("${app.rag.pdf-path}")
    private Resource pdfResource;

    private final InMemoryChatMemory inMemoryChatMemory = new InMemoryChatMemory();

    public ClaudeChatBoatServiceImpl(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    /**
     * This method runs automatically when the application starts.
     * It loads the PDF, splits it into chunks, and stores
     * them in the Vector Store.
     */
    @Override
    @PostConstruct
    public void loadPdfToVectorStore() {
        logger.info("Loading PDF into Vector Store - Starting");

        // Step 1 - Read the PDF document
        // PagePdfDocumentReader reads your PDF page by page
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfResource);

        // step 2 - Load all pages as documents
        List<Document> documents = pdfDocumentReader.get();

        // Step 3 - Split documents into smaller chunks,
        // we are using TokenTextSplitter to breaks large text into smaller pieces that fir in Claude's context window
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.split(documents);

        // Try this to read chunked data in your console.
        /* System.out.println("Total chunks after splitting: " + splitDocuments.size());
        splitDocuments.stream().forEach(i -> {System.out.println("Data~~ : " + i.getText());});*/

        // Step 4 - Store chunks in Vector (numbers) Store. Spring AI automatically converts text to vectors (embeddings) and stores them
        vectorStore.add(splitDocuments);

        // Try this to save Vector Store to a JSON file..
        // SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
        // simpleVectorStore.save(new File("vector-store.json"));

        logger.info("Loading PDF into Vector Store - Ending : "
                + splitDocuments.size() + " chunks stored in Vector Store.");
    }

    @Override
    public String ask(String question) {
        String respone = null;

        // This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
        try {
            respone = chatClient
                    .prompt()
                    .user(question)
                    .call()
                    .content();
        } catch (Exception e) {
            respone = "something went wrong!! : " + e.getMessage();
        }
        return respone;
    }

    @Override
    public Flux<String> askStram(String question) {
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
        String respone =  null;

        // This will call an AI model (Claude as of now), it will take prompt from user - question, send it to AI model, and get the answer from them!
        try {
            respone = chatClient
                    .prompt()
                    .user(question)
                    // Builder pattern with updated memory class
                    .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, userID, inChatHistoryWindowSize))
                    .call()
                    .content();

        } catch (Exception e) {
            respone = "something went wrong!! : " + e.getMessage();
        }
        return respone;
    }

    /**
     * RAG endpoint - answers questions from YOUR PDF document
     * @param question the question to ask about the PDF
     * @return answer from Claude based on PDF content
     */
    @Override
    public String askPdf(String question) {
        String respone =  null;
        try {
            respone = chatClient
                    .prompt()
                    .user(question)
                    .advisors(new QuestionAnswerAdvisor(vectorStore))
                    .call()
                    .content();
        } catch(Exception e){
            respone = "something went wrong!! : " + e.getMessage();
        }
        return respone;
    }
}
