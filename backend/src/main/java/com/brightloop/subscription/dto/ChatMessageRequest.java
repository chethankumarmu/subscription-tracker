package com.brightloop.subscription.dto;

public class ChatMessageRequest {
    private Long sessionId;
    private Long customerId;
    private String message;

    public ChatMessageRequest() {}

    public ChatMessageRequest(Long sessionId, Long customerId, String message) {
        this.sessionId = sessionId;
        this.customerId = customerId;
        this.message = message;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
