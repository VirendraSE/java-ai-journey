package com.github.virendra.java_ai_journey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    /**
     * HTTP status code e.g. 400, 404, 500
     */
    private int status;

    /**
     * Short error title e.g. "Bad Request"
     */
    private String error;

    /**
     * Detailed error message for the user
     */
    private String errorMessage;

    /**
     * The endpoint path that caused the error
     */
    private String path;

    /**
     * Exact time the error occurred
     */
    private LocalDateTime timestamp;
}