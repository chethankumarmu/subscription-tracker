package com.brightloop.subscription.support.dto;

import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;

import java.time.Instant;
import java.util.List;

public class TicketDetailResponse {
    private Long id;
    private String number;
    private Long customerId;
    private String companyName;
    private String contactName;
    private Long sessionId;
    private String subject;
    private TicketStatus status;
    private TicketPriority priority;
    private Long assignedStaffId;
    private String assignedStaffName;
    private Instant createdAt;
    private Instant resolvedAt;
    private List<ChatTranscriptMessageDto> chatTranscript;
    private List<CommentResponse> comments;

    public TicketDetailResponse() {}
    public TicketDetailResponse(Long id, String number, Long customerId, String companyName, String contactName, Long sessionId, String subject, TicketStatus status, TicketPriority priority, Long assignedStaffId, String assignedStaffName, Instant createdAt, Instant resolvedAt, List<ChatTranscriptMessageDto> chatTranscript, List<CommentResponse> comments) {
        this.id = id;
        this.number = number;
        this.customerId = customerId;
        this.companyName = companyName;
        this.contactName = contactName;
        this.sessionId = sessionId;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.assignedStaffId = assignedStaffId;
        this.assignedStaffName = assignedStaffName;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.chatTranscript = chatTranscript;
        this.comments = comments;
    }

    public static TicketDetailResponseBuilder builder() { return new TicketDetailResponseBuilder(); }
    public static class TicketDetailResponseBuilder {
        private Long id;
        private String number;
        private Long customerId;
        private String companyName;
        private String contactName;
        private Long sessionId;
        private String subject;
        private TicketStatus status;
        private TicketPriority priority;
        private Long assignedStaffId;
        private String assignedStaffName;
        private Instant createdAt;
        private Instant resolvedAt;
        private List<ChatTranscriptMessageDto> chatTranscript;
        private List<CommentResponse> comments;

        public TicketDetailResponseBuilder id(Long id) { this.id = id; return this; }
        public TicketDetailResponseBuilder number(String number) { this.number = number; return this; }
        public TicketDetailResponseBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public TicketDetailResponseBuilder companyName(String companyName) { this.companyName = companyName; return this; }
        public TicketDetailResponseBuilder contactName(String contactName) { this.contactName = contactName; return this; }
        public TicketDetailResponseBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public TicketDetailResponseBuilder subject(String subject) { this.subject = subject; return this; }
        public TicketDetailResponseBuilder status(TicketStatus status) { this.status = status; return this; }
        public TicketDetailResponseBuilder priority(TicketPriority priority) { this.priority = priority; return this; }
        public TicketDetailResponseBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public TicketDetailResponseBuilder assignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; return this; }
        public TicketDetailResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public TicketDetailResponseBuilder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public TicketDetailResponseBuilder chatTranscript(List<ChatTranscriptMessageDto> chatTranscript) { this.chatTranscript = chatTranscript; return this; }
        public TicketDetailResponseBuilder comments(List<CommentResponse> comments) { this.comments = comments; return this; }

        public TicketDetailResponse build() {
            return new TicketDetailResponse(id, number, customerId, companyName, contactName, sessionId, subject, status, priority, assignedStaffId, assignedStaffName, createdAt, resolvedAt, chatTranscript, comments);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public List<ChatTranscriptMessageDto> getChatTranscript() { return chatTranscript; }
    public void setChatTranscript(List<ChatTranscriptMessageDto> chatTranscript) { this.chatTranscript = chatTranscript; }
    public List<CommentResponse> getComments() { return comments; }
    public void setComments(List<CommentResponse> comments) { this.comments = comments; }
}
