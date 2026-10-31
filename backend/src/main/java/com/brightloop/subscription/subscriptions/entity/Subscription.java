package com.brightloop.subscription.subscriptions.entity;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.plans.entity.PlanVersion;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_version_id", nullable = false)
    private PlanVersion planVersion;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, QUOTATION, CONFIRMED, ACTIVE, CLOSED

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

    @Column(name = "close_reason")
    private String closeReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionLine> lines = new ArrayList<>();

    public Subscription() {
    }

    public Subscription(Long id, String number, Customer customer, PlanVersion planVersion, String status, Boolean isPaused, LocalDate startDate, LocalDate expiryDate, Integer paymentTermsDays, LocalDate nextBillingDate, LocalDate quotationValidUntil, String closeReason, User createdBy, Integer version, OffsetDateTime createdAt, OffsetDateTime updatedAt, List<SubscriptionLine> lines) {
        this.id = id;
        this.number = number;
        this.customer = customer;
        this.planVersion = planVersion;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lines = lines != null ? lines : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PlanVersion getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(PlanVersion planVersion) {
        this.planVersion = planVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsPaused() {
        return isPaused;
    }

    public void setIsPaused(Boolean isPaused) {
        this.isPaused = isPaused;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public LocalDate getQuotationValidUntil() {
        return quotationValidUntil;
    }

    public void setQuotationValidUntil(LocalDate quotationValidUntil) {
        this.quotationValidUntil = quotationValidUntil;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SubscriptionLine> getLines() {
        return lines;
    }

    public void setLines(List<SubscriptionLine> lines) {
        this.lines = lines;
    }

    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public Long getPlanVersionId() {
        return planVersion != null ? planVersion.getId() : null;
    }

    public Boolean getAllowPause() {
        return planVersion != null ? planVersion.getAllowPause() : true;
    }

    public Boolean getAllowRenewal() {
        return planVersion != null ? planVersion.getAllowRenewal() : true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String number;
        private Customer customer;
        private PlanVersion planVersion;
        private String status = "DRAFT";
        private Boolean isPaused = false;
        private LocalDate startDate;
        private LocalDate expiryDate;
        private Integer paymentTermsDays = 30;
        private LocalDate nextBillingDate;
        private LocalDate quotationValidUntil;
        private String closeReason;
        private User createdBy;
        private Integer version = 0;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<SubscriptionLine> lines = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder planVersion(PlanVersion planVersion) {
            this.planVersion = planVersion;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder isPaused(Boolean isPaused) {
            this.isPaused = isPaused;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder paymentTermsDays(Integer paymentTermsDays) {
            this.paymentTermsDays = paymentTermsDays;
            return this;
        }

        public Builder nextBillingDate(LocalDate nextBillingDate) {
            this.nextBillingDate = nextBillingDate;
            return this;
        }

        public Builder quotationValidUntil(LocalDate quotationValidUntil) {
            this.quotationValidUntil = quotationValidUntil;
            return this;
        }

        public Builder closeReason(String closeReason) {
            this.closeReason = closeReason;
            return this;
        }

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder version(Integer version) {
            this.version = version;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder lines(List<SubscriptionLine> lines) {
            this.lines = lines;
            return this;
        }

        public Subscription build() {
            return new Subscription(id, number, customer, planVersion, status, isPaused, startDate, expiryDate, paymentTermsDays, nextBillingDate, quotationValidUntil, closeReason, createdBy, version, createdAt, updatedAt, lines);
        }
    }
}
