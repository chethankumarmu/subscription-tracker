package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String number;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 50)
    private String status = "DRAFT";

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, columnDefinition = "char(3)")
    private String currency = "INR";

    @Column(name = "subtotal_paise", nullable = false)
    private Long subtotalPaise = 0L;

    @Column(name = "discount_paise", nullable = false)
    private Long discountPaise = 0L;

    @Column(name = "tax_paise", nullable = false)
    private Long taxPaise = 0L;

    @Column(name = "total_paise", nullable = false)
    private Long totalPaise = 0L;

    @Column(name = "amount_paid_paise", nullable = false)
    private Long amountPaidPaise = 0L;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Invoice() {}

    public Invoice(Long id, String number, Long subscriptionId, Long customerId, String status, LocalDate periodStart, LocalDate periodEnd, LocalDate issueDate, LocalDate dueDate, String currency, Long subtotalPaise, Long discountPaise, Long taxPaise, Long totalPaise, Long amountPaidPaise, Integer version, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.subscriptionId = subscriptionId;
        this.customerId = customerId;
        this.status = status != null ? status : "DRAFT";
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.issueDate = issueDate != null ? issueDate : LocalDate.now();
        this.dueDate = dueDate;
        this.currency = currency != null ? currency : "INR";
        this.subtotalPaise = subtotalPaise != null ? subtotalPaise : 0L;
        this.discountPaise = discountPaise != null ? discountPaise : 0L;
        this.taxPaise = taxPaise != null ? taxPaise : 0L;
        this.totalPaise = totalPaise != null ? totalPaise : 0L;
        this.amountPaidPaise = amountPaidPaise != null ? amountPaidPaise : 0L;
        this.version = version != null ? version : 0;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static InvoiceBuilder builder() {
        return new InvoiceBuilder();
    }

    public static class InvoiceBuilder {
        private Long id;
        private String number;
        private Long subscriptionId;
        private Long customerId;
        private String status = "DRAFT";
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private LocalDate issueDate = LocalDate.now();
        private LocalDate dueDate;
        private String currency = "INR";
        private Long subtotalPaise = 0L;
        private Long discountPaise = 0L;
        private Long taxPaise = 0L;
        private Long totalPaise = 0L;
        private Long amountPaidPaise = 0L;
        private Integer version = 0;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public InvoiceBuilder id(Long id) { this.id = id; return this; }
        public InvoiceBuilder number(String number) { this.number = number; return this; }
        public InvoiceBuilder subscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; return this; }
        public InvoiceBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public InvoiceBuilder status(String status) { this.status = status; return this; }
        public InvoiceBuilder periodStart(LocalDate periodStart) { this.periodStart = periodStart; return this; }
        public InvoiceBuilder periodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; return this; }
        public InvoiceBuilder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public InvoiceBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public InvoiceBuilder currency(String currency) { this.currency = currency; return this; }
        public InvoiceBuilder subtotalPaise(Long subtotalPaise) { this.subtotalPaise = subtotalPaise; return this; }
        public InvoiceBuilder discountPaise(Long discountPaise) { this.discountPaise = discountPaise; return this; }
        public InvoiceBuilder taxPaise(Long taxPaise) { this.taxPaise = taxPaise; return this; }
        public InvoiceBuilder totalPaise(Long totalPaise) { this.totalPaise = totalPaise; return this; }
        public InvoiceBuilder amountPaidPaise(Long amountPaidPaise) { this.amountPaidPaise = amountPaidPaise; return this; }
        public InvoiceBuilder version(Integer version) { this.version = version; return this; }
        public InvoiceBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public InvoiceBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Invoice build() {
            return new Invoice(id, number, subscriptionId, customerId, status, periodStart, periodEnd, issueDate, dueDate, currency, subtotalPaise, discountPaise, taxPaise, totalPaise, amountPaidPaise, version, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getSubtotalPaise() { return subtotalPaise; }
    public void setSubtotalPaise(Long subtotalPaise) { this.subtotalPaise = subtotalPaise; }
    public Long getDiscountPaise() { return discountPaise; }
    public void setDiscountPaise(Long discountPaise) { this.discountPaise = discountPaise; }
    public Long getTaxPaise() { return taxPaise; }
    public void setTaxPaise(Long taxPaise) { this.taxPaise = taxPaise; }
    public Long getTotalPaise() { return totalPaise; }
    public void setTotalPaise(Long totalPaise) { this.totalPaise = totalPaise; }
    public Long getAmountPaidPaise() { return amountPaidPaise; }
    public void setAmountPaidPaise(Long amountPaidPaise) { this.amountPaidPaise = amountPaidPaise; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
