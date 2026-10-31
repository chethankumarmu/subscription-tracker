package com.brightloop.subscription.notifications.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "email_outbox")
public class EmailOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "template_key")
    private String templateKey;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SENT, FAILED

    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(name = "next_attempt_at", nullable = false)
    private OffsetDateTime nextAttemptAt = OffsetDateTime.now();

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "attachment_invoice_id")
    private Long attachmentInvoiceId;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    public EmailOutbox() {
    }

    public EmailOutbox(Long id, String toAddress, String subject, String body, String templateKey, String status, Integer attempts, OffsetDateTime nextAttemptAt, String lastError, Long attachmentInvoiceId, String attachmentName, OffsetDateTime createdAt, OffsetDateTime sentAt) {
        this.id = id;
        this.toAddress = toAddress;
        this.subject = subject;
        this.body = body;
        this.templateKey = templateKey;
        this.status = status != null ? status : "PENDING";
        this.attempts = attempts != null ? attempts : 0;
        this.nextAttemptAt = nextAttemptAt != null ? nextAttemptAt : OffsetDateTime.now();
        this.lastError = lastError;
        this.attachmentInvoiceId = attachmentInvoiceId;
        this.attachmentName = attachmentName;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (nextAttemptAt == null) {
            nextAttemptAt = OffsetDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
        if (attempts == null) {
            attempts = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public OffsetDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }

    public void setNextAttemptAt(OffsetDateTime nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Long getAttachmentInvoiceId() {
        return attachmentInvoiceId;
    }

    public void setAttachmentInvoiceId(Long attachmentInvoiceId) {
        this.attachmentInvoiceId = attachmentInvoiceId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String toAddress;
        private String subject;
        private String body;
        private String templateKey;
        private String status = "PENDING";
        private Integer attempts = 0;
        private OffsetDateTime nextAttemptAt;
        private String lastError;
        private Long attachmentInvoiceId;
        private String attachmentName;
        private OffsetDateTime createdAt;
        private OffsetDateTime sentAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder toAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder templateKey(String templateKey) {
            this.templateKey = templateKey;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder attempts(Integer attempts) {
            this.attempts = attempts;
            return this;
        }

        public Builder nextAttemptAt(OffsetDateTime nextAttemptAt) {
            this.nextAttemptAt = nextAttemptAt;
            return this;
        }

        public Builder lastError(String lastError) {
            this.lastError = lastError;
            return this;
        }

        public Builder attachmentInvoiceId(Long attachmentInvoiceId) {
            this.attachmentInvoiceId = attachmentInvoiceId;
            return this;
        }

        public Builder attachmentName(String attachmentName) {
            this.attachmentName = attachmentName;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder sentAt(OffsetDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public EmailOutbox build() {
            return new EmailOutbox(id, toAddress, subject, body, templateKey, status, attempts, nextAttemptAt, lastError, attachmentInvoiceId, attachmentName, createdAt, sentAt);
        }
    }
}
