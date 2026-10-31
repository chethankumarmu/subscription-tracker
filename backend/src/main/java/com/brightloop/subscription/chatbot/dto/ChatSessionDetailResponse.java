package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import java.time.Instant;
import java.util.List;

public class ChatSessionDetailResponse {
    private Long sessionId;
    private Long customerId;
    private ChatSessionStatus status;
    private Long ticketId;
    private String ticketNumber;
    private Instant startedAt;
    private Instant endedAt;
    private List<ChatMessageDto> messages;

    public ChatSessionDetailResponse() {}

    public ChatSessionDetailResponse(Long sessionId, Long customerId, ChatSessionStatus status, Long ticketId, String ticketNumber, Instant startedAt, Instant endedAt, List<ChatMessageDto> messages) {
        this.sessionId = sessionId;
        this.customerId = customerId;
        this.status = status;
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.messages = messages;
    }

    public static ChatSessionDetailResponseBuilder builder() {
        return new ChatSessionDetailResponseBuilder();
    }

    public static class ChatSessionDetailResponseBuilder {
        private Long sessionId;
        private Long customerId;
        private ChatSessionStatus status;
        private Long ticketId;
        private String ticketNumber;
        private Instant startedAt;
        private Instant endedAt;
        private List<ChatMessageDto> messages;

        public ChatSessionDetailResponseBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public ChatSessionDetailResponseBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public ChatSessionDetailResponseBuilder status(ChatSessionStatus status) { this.status = status; return this; }
        public ChatSessionDetailResponseBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public ChatSessionDetailResponseBuilder ticketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; return this; }
        public ChatSessionDetailResponseBuilder startedAt(Instant startedAt) { this.startedAt = startedAt; return this; }
        public ChatSessionDetailResponseBuilder endedAt(Instant endedAt) { this.endedAt = endedAt; return this; }
        public ChatSessionDetailResponseBuilder messages(List<ChatMessageDto> messages) { this.messages = messages; return this; }

        public ChatSessionDetailResponse build() {
            return new ChatSessionDetailResponse(sessionId, customerId, status, ticketId, ticketNumber, startedAt, endedAt, messages);
        }
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public ChatSessionStatus getStatus() { return status; }
    public void setStatus(ChatSessionStatus status) { this.status = status; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
    public List<ChatMessageDto> getMessages() { return messages; }
    public void setMessages(List<ChatMessageDto> messages) { this.messages = messages; }
}
