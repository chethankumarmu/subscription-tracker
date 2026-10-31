package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.chatbot.entity.ChatIntent;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponse {
    private Long sessionId;
    private ChatIntent intent;
    private String type;
    private String answer;
    private ChatbotDataPayload data;
    private boolean escalated;
    private List<String> suggestions;

    public ChatResponse() {}

    public ChatResponse(Long sessionId, ChatIntent intent, String type, String answer, ChatbotDataPayload data, boolean escalated, List<String> suggestions) {
        this.sessionId = sessionId;
        this.intent = intent;
        this.type = type;
        this.answer = answer;
        this.data = data;
        this.escalated = escalated;
        this.suggestions = suggestions;
    }

    public static ChatResponseBuilder builder() {
        return new ChatResponseBuilder();
    }

    public static class ChatResponseBuilder {
        private Long sessionId;
        private ChatIntent intent;
        private String type;
        private String answer;
        private ChatbotDataPayload data;
        private boolean escalated;
        private List<String> suggestions;

        public ChatResponseBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public ChatResponseBuilder intent(ChatIntent intent) { this.intent = intent; return this; }
        public ChatResponseBuilder type(String type) { this.type = type; return this; }
        public ChatResponseBuilder answer(String answer) { this.answer = answer; return this; }
        public ChatResponseBuilder data(ChatbotDataPayload data) { this.data = data; return this; }
        public ChatResponseBuilder escalated(boolean escalated) { this.escalated = escalated; return this; }
        public ChatResponseBuilder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }

        public ChatResponse build() {
            return new ChatResponse(sessionId, intent, type, answer, data, escalated, suggestions);
        }
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public ChatIntent getIntent() { return intent; }
    public void setIntent(ChatIntent intent) { this.intent = intent; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public ChatbotDataPayload getData() { return data; }
    public void setData(ChatbotDataPayload data) { this.data = data; }
    public boolean isEscalated() { return escalated; }
    public void setEscalated(boolean escalated) { this.escalated = escalated; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
