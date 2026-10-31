package com.brightloop.subscription.support.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class SupportTicketDto {
    private Long id;
    private String number;
    private Long customerId;
    private Long sessionId;
    private String subject;
    private String status;
    private String priority;
    private Long assignedTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;
    private List<TicketCommentDto> comments;

    public SupportTicketDto() {}

    public SupportTicketDto(Long id, String number, Long customerId, Long sessionId, String subject, String status, String priority, Long assignedTo, OffsetDateTime createdAt, OffsetDateTime resolvedAt, List<TicketCommentDto> comments) {
        this.id = id;
        this.number = number;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.comments = comments;
    }

    public Long getId() { return id; }
    public String getNumber() { return number; }
    public Long getCustomerId() { return customerId; }
    public Long getSessionId() { return sessionId; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public Long getAssignedTo() { return assignedTo; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public List<TicketCommentDto> getComments() { return comments; }
}

