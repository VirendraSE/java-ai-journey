package com.github.virendra.java_ai_journey.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for Vector Store operations.
 * Defines contract for loading and managing
 * PDF documents in the Vector Store.
 *
 * @author Virendra
 * @version 1.0.0
 */
public interface VectorStorageService {

    /**
     * Loads pre-loaded PDF into Vector Store on startup
     */
    void loadPdfToVectorStore();

    /**
     * Dynamically loads user uploaded PDF per session
     * @param file      uploaded PDF file
     * @param sessionId unique session identifier
     */
    void loadUserPdfToVectorStore(
            MultipartFile file,
            String sessionId) throws IOException;

    /**
     * Clears session tracking for a given sessionId
     * @param sessionId session to clear
     */
    void clearSession(String sessionId);
}
