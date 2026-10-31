package com.brightloop.subscription.model;

import jakarta.persistence.*;
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

    @Column(nullable = false, length = 50)
    private String status = "BOT";

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "ticket_id")
    private Long ticketId;

    public ChatSession() {}

    public ChatSession(Long id, Long userId, Long customerId, String status, OffsetDateTime startedAt, OffsetDateTime endedAt, Long ticketId) {
        this.id = id;
        this.userId = userId;
        this.customerId = customerId;
        this.status = status != null ? status : "BOT";
        this.startedAt = startedAt != null ? startedAt : OffsetDateTime.now();
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
        private String status = "BOT";
        private OffsetDateTime startedAt = OffsetDateTime.now();
        private OffsetDateTime endedAt;
        private Long ticketId;

        public ChatSessionBuilder id(Long id) { this.id = id; return this; }
        public ChatSessionBuilder userId(Long userId) { this.userId = userId; return this; }
        public ChatSessionBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public ChatSessionBuilder status(String status) { this.status = status; return this; }
        public ChatSessionBuilder startedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; return this; }
        public ChatSessionBuilder endedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; return this; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
}
