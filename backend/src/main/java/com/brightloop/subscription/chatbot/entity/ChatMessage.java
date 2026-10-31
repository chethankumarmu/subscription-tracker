package com.brightloop.subscription.chatbot.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MessageSender sender;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(length = 100)
    private String intent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ChatMessage() {}

    public ChatMessage(Long id, Long sessionId, MessageSender sender, String body, String intent, Instant createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.body = body;
        this.intent = intent;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static ChatMessageBuilder builder() {
        return new ChatMessageBuilder();
    }

    public static class ChatMessageBuilder {
        private Long id;
        private Long sessionId;
        private MessageSender sender = MessageSender.BOT;
        private String body;
        private String intent;
        private Instant createdAt = Instant.now();

        public ChatMessageBuilder id(Long id) { this.id = id; return this; }
        public ChatMessageBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public ChatMessageBuilder sender(MessageSender sender) { this.sender = sender; return this; }
        public ChatMessageBuilder sender(String sender) {
            try { this.sender = MessageSender.valueOf(sender.toUpperCase()); }
            catch (Exception e) { this.sender = MessageSender.BOT; }
            return this;
        }
        public ChatMessageBuilder body(String body) { this.body = body; return this; }
        public ChatMessageBuilder intent(String intent) { this.intent = intent; return this; }
        public ChatMessageBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public ChatMessageBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt != null ? createdAt.toInstant() : Instant.now(); return this; }

        public ChatMessage build() {
            return new ChatMessage(id, sessionId, sender, body, intent, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public MessageSender getSender() { return sender; }
    public void setSender(MessageSender sender) { this.sender = sender; }
    public void setSender(String sender) {
        try { this.sender = MessageSender.valueOf(sender.toUpperCase()); }
        catch (Exception e) { this.sender = MessageSender.BOT; }
    }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
