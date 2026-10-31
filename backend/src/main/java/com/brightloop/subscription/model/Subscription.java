package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String number;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "plan_version_id", nullable = false)
    private Long planVersionId;

    @Column(nullable = false, length = 50)
    private String status = "DRAFT";

    @Column(name = "is_paused", nullable = false)
    private Boolean isPaused = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "payment_terms_days", nullable = false)
    private Integer paymentTermsDays = 30;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "quotation_valid_until")
    private LocalDate quotationValidUntil;

    @Column(name = "close_reason", columnDefinition = "text")
    private String closeReason;

    @Column(name = "created_by")
    private Long createdBy;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Subscription() {}

    public Subscription(Long id, String number, Long customerId, Long planVersionId, String status, Boolean isPaused, LocalDate startDate, LocalDate expiryDate, Integer paymentTermsDays, LocalDate nextBillingDate, LocalDate quotationValidUntil, String closeReason, Long createdBy, Integer version, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.customerId = customerId;
        this.planVersionId = planVersionId;
        this.status = status != null ? status : "DRAFT";
        this.isPaused = isPaused != null ? isPaused : false;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.paymentTermsDays = paymentTermsDays != null ? paymentTermsDays : 30;
        this.nextBillingDate = nextBillingDate;
        this.quotationValidUntil = quotationValidUntil;
        this.closeReason = closeReason;
        this.createdBy = createdBy;
        this.version = version != null ? version : 0;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static SubscriptionBuilder builder() {
        return new SubscriptionBuilder();
    }

    public static class SubscriptionBuilder {
        private Long id;
        private String number;
        private Long customerId;
        private Long planVersionId;
        private String status = "DRAFT";
        private Boolean isPaused = false;
        private LocalDate startDate;
        private LocalDate expiryDate;
        private Integer paymentTermsDays = 30;
        private LocalDate nextBillingDate;
        private LocalDate quotationValidUntil;
        private String closeReason;
        private Long createdBy;
        private Integer version = 0;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public SubscriptionBuilder id(Long id) { this.id = id; return this; }
        public SubscriptionBuilder number(String number) { this.number = number; return this; }
        public SubscriptionBuilder customerId(Long customerId) { this.customerId = customerId; return this; }
        public SubscriptionBuilder planVersionId(Long planVersionId) { this.planVersionId = planVersionId; return this; }
        public SubscriptionBuilder status(String status) { this.status = status; return this; }
        public SubscriptionBuilder isPaused(Boolean isPaused) { this.isPaused = isPaused; return this; }
        public SubscriptionBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public SubscriptionBuilder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public SubscriptionBuilder paymentTermsDays(Integer paymentTermsDays) { this.paymentTermsDays = paymentTermsDays; return this; }
        public SubscriptionBuilder nextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; return this; }
        public SubscriptionBuilder quotationValidUntil(LocalDate quotationValidUntil) { this.quotationValidUntil = quotationValidUntil; return this; }
        public SubscriptionBuilder closeReason(String closeReason) { this.closeReason = closeReason; return this; }
        public SubscriptionBuilder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }
        public SubscriptionBuilder version(Integer version) { this.version = version; return this; }
        public SubscriptionBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SubscriptionBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Subscription build() {
            return new Subscription(id, number, customerId, planVersionId, status, isPaused, startDate, expiryDate, paymentTermsDays, nextBillingDate, quotationValidUntil, closeReason, createdBy, version, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getPlanVersionId() { return planVersionId; }
    public void setPlanVersionId(Long planVersionId) { this.planVersionId = planVersionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getIsPaused() { return isPaused; }
    public void setIsPaused(Boolean isPaused) { this.isPaused = isPaused; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getPaymentTermsDays() { return paymentTermsDays; }
    public void setPaymentTermsDays(Integer paymentTermsDays) { this.paymentTermsDays = paymentTermsDays; }
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    public LocalDate getQuotationValidUntil() { return quotationValidUntil; }
    public void setQuotationValidUntil(LocalDate quotationValidUntil) { this.quotationValidUntil = quotationValidUntil; }
    public String getCloseReason() { return closeReason; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
