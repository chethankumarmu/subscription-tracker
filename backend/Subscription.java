package com.subscriptiontracker.subscription;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    public enum Status { DRAFT, QUOTATION, CONFIRMED, ACTIVE, CLOSED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String number;
    @Column(name = "customer_id", nullable = false) private Long customerId;
    @Column(name = "plan_version_id", nullable = false) private Long planVersionId;
    @Column(name = "variant_id") private Long variantId;
    @Column(nullable = false) private int quantity = 1;
    @Column(name = "unit_price", nullable = false) private long unitPrice; // paise, locked
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Status status = Status.DRAFT;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "expiry_date") private LocalDate expiryDate;
    @Column(name = "next_billing_date") private LocalDate nextBillingDate;
    @Column(name = "is_paused", nullable = false) private boolean paused;
    @Column(name = "quotation_template_id") private Long quotationTemplateId;
    @Column(name = "quotation_valid_until") private LocalDate quotationValidUntil;
    @Version private Long version; // optimistic locking

    public Long getId() { return id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getPlanVersionId() { return planVersionId; }
    public void setPlanVersionId(Long planVersionId) { this.planVersionId = planVersionId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getUnitPrice() { return unitPrice; }
    public void setUnitPrice(long unitPrice) { this.unitPrice = unitPrice; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public Long getQuotationTemplateId() { return quotationTemplateId; }
    public void setQuotationTemplateId(Long id) { this.quotationTemplateId = id; }
    public LocalDate getQuotationValidUntil() { return quotationValidUntil; }
    public void setQuotationValidUntil(LocalDate d) { this.quotationValidUntil = d; }
    public Long getVersion() { return version; }
}
