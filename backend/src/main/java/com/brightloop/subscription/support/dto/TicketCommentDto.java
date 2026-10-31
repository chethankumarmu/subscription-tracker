package com.brightloop.subscription.support.dto;

import java.time.OffsetDateTime;

public class TicketCommentDto {
    private Long id;
    private Long ticketId;
    private Long authorId;
    private String body;
    private Boolean isInternal;
    private OffsetDateTime createdAt;

    public TicketCommentDto() {}

    public TicketCommentDto(Long id, Long ticketId, Long authorId, String body, Boolean isInternal, OffsetDateTime createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.body = body;
        this.isInternal = isInternal;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public Long getAuthorId() { return authorId; }
    public String getBody() { return body; }
    public Boolean getIsInternal() { return isInternal; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

