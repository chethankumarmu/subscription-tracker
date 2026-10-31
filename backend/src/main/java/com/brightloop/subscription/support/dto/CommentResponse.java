package com.brightloop.subscription.support.dto;

import java.time.Instant;

public class CommentResponse {
    private Long id;
    private Long ticketId;
    private Long authorId;
    private String authorName;
    private String body;
    private boolean internal;
    private Instant createdAt;

    public CommentResponse() {}
    public CommentResponse(Long id, Long ticketId, Long authorId, String authorName, String body, boolean internal, Instant createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.body = body;
        this.internal = internal;
        this.createdAt = createdAt;
    }

    public static CommentResponseBuilder builder() { return new CommentResponseBuilder(); }
    public static class CommentResponseBuilder {
        private Long id;
        private Long ticketId;
        private Long authorId;
        private String authorName;
        private String body;
        private boolean internal;
        private Instant createdAt;
        public CommentResponseBuilder id(Long id) { this.id = id; return this; }
        public CommentResponseBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public CommentResponseBuilder authorId(Long authorId) { this.authorId = authorId; return this; }
        public CommentResponseBuilder authorName(String authorName) { this.authorName = authorName; return this; }
        public CommentResponseBuilder body(String body) { this.body = body; return this; }
        public CommentResponseBuilder internal(boolean internal) { this.internal = internal; return this; }
        public CommentResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public CommentResponse build() { return new CommentResponse(id, ticketId, authorId, authorName, body, internal, createdAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
