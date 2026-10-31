package com.brightloop.subscription.support.dto;

import java.time.Instant;

public class ChatTranscriptMessageDto {
    private Long id;
    private String sender;
    private String body;
    private String intent;
    private Instant createdAt;

    public ChatTranscriptMessageDto() {}
    public ChatTranscriptMessageDto(Long id, String sender, String body, String intent, Instant createdAt) {
        this.id = id;
        this.sender = sender;
        this.body = body;
        this.intent = intent;
        this.createdAt = createdAt;
    }

    public static ChatTranscriptMessageDtoBuilder builder() { return new ChatTranscriptMessageDtoBuilder(); }
    public static class ChatTranscriptMessageDtoBuilder {
        private Long id;
        private String sender;
        private String body;
        private String intent;
        private Instant createdAt;
        public ChatTranscriptMessageDtoBuilder id(Long id) { this.id = id; return this; }
        public ChatTranscriptMessageDtoBuilder sender(String sender) { this.sender = sender; return this; }
        public ChatTranscriptMessageDtoBuilder body(String body) { this.body = body; return this; }
        public ChatTranscriptMessageDtoBuilder intent(String intent) { this.intent = intent; return this; }
        public ChatTranscriptMessageDtoBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ChatTranscriptMessageDto build() { return new ChatTranscriptMessageDto(id, sender, body, intent, createdAt); }
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
