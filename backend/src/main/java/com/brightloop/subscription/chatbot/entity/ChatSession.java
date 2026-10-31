package com.brightloop.subscription.chatbot.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_id")
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatSessionStatus status = ChatSessionStatus.BOT;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "ticket_id")
    private Long ticketId;

    public ChatSession() {}

    public ChatSession(Long id, Long userId, Long customerId, ChatSessionStatus status, Instant startedAt, Instant endedAt, Long ticketId) {
        this.id = id;
        this.userId = userId;
        this.customerId = customerId;
        this.status = status != null ? status : ChatSessionStatus.BOT;
        this.startedAt = startedAt != null ? startedAt : Instant.now();
        this.endedAt = endedAt;
        this.ticketId = ticketId;
    }

    public static ChatSessionBuilder builder() {
        return new ChatSessionBuilder();
    }

    public static class ChatSessionBuilder {
        private Long id;
        private Long userId;
        private Long customerId;
        private ChatSessionStatus status = ChatSessionStatus.BOT;
        private Instant startedAt = Instant.now();
        private Instant endedAt;
        private Long ticketId;

        public ChatSessionBuilder id(Long id) { this.id = id; return this; }
        public ChatSessionBuilder userId(Long userId) { this.userId = userId; return this; }
        public ChatSessionBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public ChatSessionBuilder status(ChatSessionStatus status) { this.status = status; return this; }
        public ChatSessionBuilder status(String status) {
            try { this.status = ChatSessionStatus.valueOf(status.toUpperCase()); }
            catch (Exception e) { this.status = ChatSessionStatus.BOT; }
            return this;
        }
        public ChatSessionBuilder startedAt(Instant startedAt) { this.startedAt = startedAt; return this; }
        public ChatSessionBuilder startedAt(OffsetDateTime startedAt) { this.startedAt = startedAt != null ? startedAt.toInstant() : Instant.now(); return this; }
        public ChatSessionBuilder endedAt(Instant endedAt) { this.endedAt = endedAt; return this; }
        public ChatSessionBuilder endedAt(OffsetDateTime endedAt) { this.endedAt = endedAt != null ? endedAt.toInstant() : null; return this; }
        public ChatSessionBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }

        public ChatSession build() {
            return new ChatSession(id, userId, customerId, status, startedAt, endedAt, ticketId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public ChatSessionStatus getStatus() { return status; }
    public void setStatus(ChatSessionStatus status) { this.status = status; }
    public void setStatus(String status) {
        try { this.status = ChatSessionStatus.valueOf(status.toUpperCase()); }
        catch (Exception e) { this.status = ChatSessionStatus.BOT; }
    }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
}
