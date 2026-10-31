package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.chatbot.entity.ChatIntent;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponse {
    private Long sessionId;
    private ChatIntent intent;
    private String type;
    private String answer;
    private ChatbotDataPayload data;
    private boolean escalated;
    private List<String> suggestions;
}

