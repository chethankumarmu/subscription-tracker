package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String number;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 50)
    private String status = "OPEN";

    @Column(nullable = false, length = 50)
    private String priority = "NORMAL";

    @Column(name = "assigned_staff_id")
    private Long assignedStaffId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public SupportTicket() {}

    public SupportTicket(Long id, String number, Long customerId, Long sessionId, String subject, String status, String priority, Long assignedStaffId, OffsetDateTime createdAt, OffsetDateTime resolvedAt, Integer version) {
        this.id = id;
        this.number = number;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.subject = subject;
        this.status = status != null ? status : "OPEN";
        this.priority = priority != null ? priority : "NORMAL";
        this.assignedStaffId = assignedStaffId;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.resolvedAt = resolvedAt;
        this.version = version != null ? version : 0;
    }

    public static SupportTicketBuilder builder() {
        return new SupportTicketBuilder();
    }

    public static class SupportTicketBuilder {
        private Long id;
        private String number;
        private Long customerId;
        private Long sessionId;
        private String subject;
        private String status = "OPEN";
        private String priority = "NORMAL";
        private Long assignedStaffId;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime resolvedAt;
        private Integer version = 0;

        public SupportTicketBuilder id(Long id) { this.id = id; return this; }
        public SupportTicketBuilder number(String number) { this.number = number; return this; }
        public SupportTicketBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public SupportTicketBuilder sessionId(Long sessionId) { this.sessionId = sessionId; return this; }
        public SupportTicketBuilder subject(String subject) { this.subject = subject; return this; }
        public SupportTicketBuilder status(String status) { this.status = status; return this; }
        public SupportTicketBuilder priority(String priority) { this.priority = priority; return this; }
        public SupportTicketBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public SupportTicketBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SupportTicketBuilder resolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public SupportTicketBuilder version(Integer version) { this.version = version; return this; }

        public SupportTicket build() {
            return new SupportTicket(id, number, customerId, sessionId, subject, status, priority, assignedStaffId, createdAt, resolvedAt, version);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
