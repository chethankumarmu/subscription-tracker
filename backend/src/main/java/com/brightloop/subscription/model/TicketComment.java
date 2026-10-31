package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public TicketComment() {}

    public TicketComment(Long id, Long ticketId, Long authorId, String body, Boolean isInternal, OffsetDateTime createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.body = body;
        this.isInternal = isInternal != null ? isInternal : false;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
    }

    public static TicketCommentBuilder builder() {
        return new TicketCommentBuilder();
    }

    public static class TicketCommentBuilder {
        private Long id;
        private Long ticketId;
        private Long authorId;
        private String body;
        private Boolean isInternal = false;
        private OffsetDateTime createdAt = OffsetDateTime.now();

        public TicketCommentBuilder id(Long id) { this.id = id; return this; }
        public TicketCommentBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public TicketCommentBuilder authorId(Long authorId) { this.authorId = authorId; return this; }
        public TicketCommentBuilder body(String body) { this.body = body; return this; }
        public TicketCommentBuilder isInternal(Boolean isInternal) { this.isInternal = isInternal; return this; }
        public TicketCommentBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TicketComment build() {
            return new TicketComment(id, ticketId, authorId, body, isInternal, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Boolean getIsInternal() { return isInternal; }
    public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

