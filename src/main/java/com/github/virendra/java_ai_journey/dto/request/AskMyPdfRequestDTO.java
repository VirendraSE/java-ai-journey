package com.github.virendra.java_ai_journey.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AskMyPdfRequestDTO {
    /**
     * Unique identifier per user session
     * Used to track which PDF belongs to which user
     */
    private String sessionId;

    /**
     * The question user wants to ask about their PDF
     */
    private String question;
}
