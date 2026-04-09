package com.github.virendra.java_ai_journey.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    /**
     * Unique identifier for conversation session
     */
    @NotBlank(message = "User name cannot be empty")
    @Size(min = 2, max = 50,
            message = "User name must be between 2 and 50 characters")
    private String userName;

    /**
     * Question to ask AI
     */
    @NotBlank(message = "Question cannot be empty")
    @Size(min = 2, max = 100,
            message = "Question must be between 2 and 100 characters")
    private String question;
}
