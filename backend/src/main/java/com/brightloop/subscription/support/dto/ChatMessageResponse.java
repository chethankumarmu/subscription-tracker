package com.brightloop.subscription.support.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ChatMessageResponse {
    private Long sessionId;
    private String sender;
    private String reply;
    private String intent;
    private Boolean escalated;
    private String ticketNumber;
    private List<String> suggestions;
    private OffsetDateTime timestamp;

    public ChatMessageResponse() {}

    public ChatMessageResponse(Long sessionId, String sender, String reply, String intent, Boolean escalated, String ticketNumber, List<String> suggestions, OffsetDateTime timestamp) {
        this.sessionId = sessionId;
        this.sender = sender;
        this.reply = reply;
        this.intent = intent;
        this.escalated = escalated;
        this.ticketNumber = ticketNumber;
        this.suggestions = suggestions;
        this.timestamp = timestamp;
    }

    public static ChatMessageResponseBuilder builder() {
        return new ChatMessageResponseBuilder();
    }

    public static class ChatMessageResponseBuilder {
        private Long sessionId;
        private String sender;
        private String reply;
        private String intent;
        private Boolean escalated;
        private String ticketNumber;
        private List<String> suggestions;
        private OffsetDateTime timestamp;

        public ChatMessageResponseBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public ChatMessageResponseBuilder sender(String sender) { this.sender = sender; return this; }
        public ChatMessageResponseBuilder reply(String reply) { this.reply = reply; return this; }
        public ChatMessageResponseBuilder intent(String intent) { this.intent = intent; return this; }
        public ChatMessageResponseBuilder escalated(Boolean escalated) { this.escalated = escalated; return this; }
        public ChatMessageResponseBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public ChatMessageResponseBuilder suggestions(List<String> suggestions) { this.suggestions = suggestions; return this; }
        public ChatMessageResponseBuilder timestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ChatMessageResponse build() {
            return new ChatMessageResponse(sessionId, sender, reply, intent, escalated, ticketNumber, suggestions, timestamp);
        }
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public Boolean getEscalated() { return escalated; }
    public void setEscalated(Boolean escalated) { this.escalated = escalated; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}

