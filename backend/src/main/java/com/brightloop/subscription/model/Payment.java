package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(nullable = false, length = 50)
    private String method;

    @Column(name = "amount_paise", nullable = false)
    private Long amountPaise;

    @Column(name = "paid_on", nullable = false)
    private LocalDate paidOn = LocalDate.now();

    @Column(length = 255)
    private String reference;

    @Column(columnDefinition = "text")
    private String note;

    @Column(name = "recorded_by", nullable = false)
    private Long recordedBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Payment() {}

    public Payment(Long id, Long invoiceId, String method, Long amountPaise, LocalDate paidOn, String reference, String note, Long recordedBy, OffsetDateTime createdAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.method = method;
        this.amountPaise = amountPaise;
        this.paidOn = paidOn != null ? paidOn : LocalDate.now();
        this.reference = reference;
        this.note = note;
        this.recordedBy = recordedBy;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
    }

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private Long id;
        private Long invoiceId;
        private String method;
        private Long amountPaise;
        private LocalDate paidOn = LocalDate.now();
        private String reference;
        private String note;
        private Long recordedBy;
        private OffsetDateTime createdAt = OffsetDateTime.now();

        public PaymentBuilder id(Long id) { this.id = id; return this; }
        public PaymentBuilder invoiceId(Long invoiceId) { this.invoiceId = invoiceId; return this; }
        public PaymentBuilder method(String method) { this.method = method; return this; }
        public PaymentBuilder amountPaise(Long amountPaise) { this.amountPaise = amountPaise; return this; }
        public PaymentBuilder paidOn(LocalDate paidOn) { this.paidOn = paidOn; return this; }
        public PaymentBuilder reference(String reference) { this.reference = reference; return this; }
        public PaymentBuilder note(String note) { this.note = note; return this; }
        public PaymentBuilder recordedBy(Long recordedBy) { this.recordedBy = recordedBy; return this; }
        public PaymentBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Payment build() {
            return new Payment(id, invoiceId, method, amountPaise, paidOn, reference, note, recordedBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(Long amountPaise) { this.amountPaise = amountPaise; }
    public LocalDate getPaidOn() { return paidOn; }
    public void setPaidOn(LocalDate paidOn) { this.paidOn = paidOn; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

