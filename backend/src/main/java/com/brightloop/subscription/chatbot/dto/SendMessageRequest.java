package com.brightloop.subscription.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

public class SendMessageRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    public SendMessageRequest() {}
    public SendMessageRequest(String message) { this.message = message; }

    public static SendMessageRequestBuilder builder() { return new SendMessageRequestBuilder(); }
    public static class SendMessageRequestBuilder {
        private String message;
        public SendMessageRequestBuilder message(String message) { this.message = message; return this; }
        public SendMessageRequest build() { return new SendMessageRequest(message); }
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
