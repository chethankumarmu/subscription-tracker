package com.brightloop.subscription.chatbot.dto;

import java.time.Instant;

public class ChatMessageDto {
    private Long id;
    private String sender;
    private String body;
    private String intent;
    private Instant createdAt;

    public ChatMessageDto() {}

    public ChatMessageDto(Long id, String sender, String body, String intent, Instant createdAt) {
        this.id = id;
        this.sender = sender;
        this.body = body;
        this.intent = intent;
        this.createdAt = createdAt;
    }

    public static ChatMessageDtoBuilder builder() {
        return new ChatMessageDtoBuilder();
    }

    public static class ChatMessageDtoBuilder {
        private Long id;
        private String sender;
        private String body;
        private String intent;
        private Instant createdAt;

        public ChatMessageDtoBuilder id(Long id) { this.id = id; return this; }
        public ChatMessageDtoBuilder sender(String sender) { this.sender = sender; return this; }
        public ChatMessageDtoBuilder body(String body) { this.body = body; return this; }
        public ChatMessageDtoBuilder intent(String intent) { this.intent = intent; return this; }
        public ChatMessageDtoBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public ChatMessageDto build() {
            return new ChatMessageDto(id, sender, body, intent, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
