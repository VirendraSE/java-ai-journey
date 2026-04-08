package com.github.virendra.java_ai_journey.service.impl;

import com.github.virendra.java_ai_journey.service.VectorStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VectorStorageServiceImpl implements VectorStorageService {
    private final VectorStore vectorStore;

    // Reads the pdf path from application.properties
    @Value("${app.rag.pdf-path}")
    private Resource pdfResource;

    // Tracks which sessionIds have PDF loaded
    // Key = sessionId, Value = true if PDF already loaded
    private final Map<String, Boolean> sessionPdfLoaded
            = new HashMap<>();

    public VectorStorageServiceImpl(VectorStore vectorStore) {
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
        log.info("Loading PDF into Vector Store - Starting");

        // Step 1 - Read the PDF document
        // PagePdfDocumentReader reads your PDF page by page
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdfResource);

        // step 2 - Load all pages as documents
        List<Document> documents = pdfDocumentReader.get();

        log.info("Total pages read from PDF: {}", documents.size());

        // Step 3 - Split documents into smaller chunks,
        // we are using TokenTextSplitter to breaks large text into smaller pieces that fir in Claude's context window
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.split(documents);

        // Try this to read chunked data in your console.
        /* log.info("Total chunks after splitting: {}", splitDocuments.size());
        splitDocuments.stream().forEach(i -> { log.info("Data~~ : {}" , i.getText());});*/

        // Step 4 - Store chunks in Vector (numbers) Store. Spring AI automatically converts text to vectors (embeddings) and stores them
        vectorStore.add(splitDocuments);

        // Try this to save Vector Store to a JSON file..
        // SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
        // simpleVectorStore.save(new File("vector-store.json"));

        log.info("Loading PDF into Vector Store - Ending : "
                + splitDocuments.size() + " chunks stored in Vector Store.");
    }

    /**
     * Dynamically loads user uploaded PDF per session.
     * Skips loading if sessionId already has PDF loaded.
     */
    @Override
    public void loadUserPdfToVectorStore(
            MultipartFile file,
            String sessionId) throws IOException {

        // Skip if session already has PDF loaded
        if (sessionPdfLoaded.getOrDefault(sessionId, false)) {
            log.debug("Session {} already has PDF loaded - skipping to load it again!!!!", sessionId);
            return;
        }

        log.info("START : Loading user PDF for session: {}", sessionId);

        // Step 1 - Convert MultipartFile to Resource
        Resource uploadedPdf = new InputStreamResource(
                file.getInputStream());

        // Step 2 - Read the PDF
        PagePdfDocumentReader pdfReader =
                new PagePdfDocumentReader(uploadedPdf);
        List<Document> documents = pdfReader.get();

        log.info(" Total pages to read: {}", documents.size());

        // Step 3 - Split into chunks
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(documents);

        // Step 4 - Tag each chunk with sessionId metadata
        splitDocuments.forEach(doc ->
                doc.getMetadata().put("sessionId", sessionId));

        // Step 5 - Store in Vector Store
        vectorStore.add(splitDocuments);

        // Step 6 - Mark session as loaded
        sessionPdfLoaded.put(sessionId, true);

        log.info("END : User PDF loaded for session: {} - {} chunks stored." , sessionId , splitDocuments.size());
    }

    /**
     * Clears session tracking so user can upload
     * a new PDF for the same sessionId.
     */
    @Override
    public void clearSession(String sessionId) {
        sessionPdfLoaded.remove(sessionId);
        log.info("Session cleared: {}" , sessionId);
    }
}
