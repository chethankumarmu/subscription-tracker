package com.brightloop.subscription.billing.entity;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", uniqueConstraints = {
        @UniqueConstraint(name = "uq_invoices_number", columnNames = {"number"}),
        @UniqueConstraint(name = "uq_invoices_subscription_period", columnNames = {"subscription_id", "period_start"})
})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, CONFIRMED, PAID, CANCELLED

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 3)
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();

    public Invoice() {
    }

    public Invoice(Long id, String number, Subscription subscription, Customer customer, String status, LocalDate periodStart, LocalDate periodEnd, LocalDate issueDate, LocalDate dueDate, String currency, Long subtotalPaise, Long discountPaise, Long taxPaise, Long totalPaise, Long amountPaidPaise, Integer version, OffsetDateTime createdAt, OffsetDateTime updatedAt, List<InvoiceLine> lines) {
        this.id = id;
        this.number = number;
        this.subscription = subscription;
        this.customer = customer;
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
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (currency == null) {
            currency = "INR";
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getSubtotalPaise() {
        return subtotalPaise;
    }

    public void setSubtotalPaise(Long subtotalPaise) {
        this.subtotalPaise = subtotalPaise;
    }

    public Long getDiscountPaise() {
        return discountPaise;
    }

    public void setDiscountPaise(Long discountPaise) {
        this.discountPaise = discountPaise;
    }

    public Long getTaxPaise() {
        return taxPaise;
    }

    public void setTaxPaise(Long taxPaise) {
        this.taxPaise = taxPaise;
    }

    public Long getTotalPaise() {
        return totalPaise;
    }

    public void setTotalPaise(Long totalPaise) {
        this.totalPaise = totalPaise;
    }

    public Long getAmountPaidPaise() {
        return amountPaidPaise;
    }

    public void setAmountPaidPaise(Long amountPaidPaise) {
        this.amountPaidPaise = amountPaidPaise;
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

    public List<InvoiceLine> getLines() {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines) {
        this.lines = lines;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String number;
        private Subscription subscription;
        private Customer customer;
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
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<InvoiceLine> lines = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder subscription(Subscription subscription) {
            this.subscription = subscription;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder periodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public Builder periodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public Builder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder subtotalPaise(Long subtotalPaise) {
            this.subtotalPaise = subtotalPaise;
            return this;
        }

        public Builder discountPaise(Long discountPaise) {
            this.discountPaise = discountPaise;
            return this;
        }

        public Builder taxPaise(Long taxPaise) {
            this.taxPaise = taxPaise;
            return this;
        }

        public Builder totalPaise(Long totalPaise) {
            this.totalPaise = totalPaise;
            return this;
        }

        public Builder amountPaidPaise(Long amountPaidPaise) {
            this.amountPaidPaise = amountPaidPaise;
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

        public Builder lines(List<InvoiceLine> lines) {
            this.lines = lines;
            return this;
        }

        public Invoice build() {
            return new Invoice(id, number, subscription, customer, status, periodStart, periodEnd, issueDate, dueDate, currency, subtotalPaise, discountPaise, taxPaise, totalPaise, amountPaidPaise, version, createdAt, updatedAt, lines);
        }
    }
}
