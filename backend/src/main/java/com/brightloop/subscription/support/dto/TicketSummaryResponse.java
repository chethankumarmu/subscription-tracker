package com.brightloop.subscription.support.dto;

import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;

import java.time.Instant;

public class TicketSummaryResponse {
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

    public TicketSummaryResponse() {}
    public TicketSummaryResponse(Long id, String number, Long customerId, String companyName, String contactName, Long sessionId, String subject, TicketStatus status, TicketPriority priority, Long assignedStaffId, String assignedStaffName, Instant createdAt, Instant resolvedAt) {
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
    }

    public static TicketSummaryResponseBuilder builder() { return new TicketSummaryResponseBuilder(); }
    public static class TicketSummaryResponseBuilder {
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

        public TicketSummaryResponseBuilder id(Long id) { this.id = id; return this; }
        public TicketSummaryResponseBuilder number(String number) { this.number = number; return this; }
        public TicketSummaryResponseBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public TicketSummaryResponseBuilder companyName(String companyName) { this.companyName = companyName; return this; }
        public TicketSummaryResponseBuilder contactName(String contactName) { this.contactName = contactName; return this; }
        public TicketSummaryResponseBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public TicketSummaryResponseBuilder subject(String subject) { this.subject = subject; return this; }
        public TicketSummaryResponseBuilder status(TicketStatus status) { this.status = status; return this; }
        public TicketSummaryResponseBuilder priority(TicketPriority priority) { this.priority = priority; return this; }
        public TicketSummaryResponseBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public TicketSummaryResponseBuilder assignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; return this; }
        public TicketSummaryResponseBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public TicketSummaryResponseBuilder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public TicketSummaryResponse build() {
            return new TicketSummaryResponse(id, number, customerId, companyName, contactName, sessionId, subject, status, priority, assignedStaffId, assignedStaffName, createdAt, resolvedAt);
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
}
