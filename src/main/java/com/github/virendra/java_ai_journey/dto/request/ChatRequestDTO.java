package com.github.virendra.java_ai_journey.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    private String userName;
    private String question;
}
