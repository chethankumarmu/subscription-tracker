package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "discount_redemptions")
public class DiscountRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_id", nullable = false)
    private Long discountId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "amount_paise", nullable = false)
    private Long amountPaise;

    @Column(name = "redeemed_at", nullable = false)
    private OffsetDateTime redeemedAt = OffsetDateTime.now();

    public DiscountRedemption() {}

    public DiscountRedemption(Long id, Long discountId, Long invoiceId, Long subscriptionId, Long amountPaise, OffsetDateTime redeemedAt) {
        this.id = id;
        this.discountId = discountId;
        this.invoiceId = invoiceId;
        this.subscriptionId = subscriptionId;
        this.amountPaise = amountPaise;
        this.redeemedAt = redeemedAt != null ? redeemedAt : OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDiscountId() { return discountId; }
    public void setDiscountId(Long discountId) { this.discountId = discountId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public Long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(Long amountPaise) { this.amountPaise = amountPaise; }
    public OffsetDateTime getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(OffsetDateTime redeemedAt) { this.redeemedAt = redeemedAt; }
}

