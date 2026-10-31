package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subscription_lines")
public class SubscriptionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price_paise", nullable = false)
    private Long unitPricePaise;

    @Column(name = "tax_rule_id")
    private Long taxRuleId;

    @Column(name = "line_total_paise", nullable = false)
    private Long lineTotalPaise;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public SubscriptionLine() {}

    public SubscriptionLine(Long id, Long subscriptionId, Long productId, Long variantId, Integer quantity, Long unitPricePaise, Long taxRuleId, Long lineTotalPaise, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPricePaise = unitPricePaise;
        this.taxRuleId = taxRuleId;
        this.lineTotalPaise = lineTotalPaise;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static SubscriptionLineBuilder builder() {
        return new SubscriptionLineBuilder();
    }

    public static class SubscriptionLineBuilder {
        private Long id;
        private Long subscriptionId;
        private Long productId;
        private Long variantId;
        private Integer quantity = 1;
        private Long unitPricePaise;
        private Long taxRuleId;
        private Long lineTotalPaise;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public SubscriptionLineBuilder id(Long id) { this.id = id; return this; }
        public SubscriptionLineBuilder subscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; return this; }
        public SubscriptionLineBuilder productId(Long productId) { this.productId = productId; return this; }
        public SubscriptionLineBuilder variantId(Long variantId) { this.variantId = variantId; return this; }
        public SubscriptionLineBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public SubscriptionLineBuilder unitPricePaise(Long unitPricePaise) { this.unitPricePaise = unitPricePaise; return this; }
        public SubscriptionLineBuilder taxRuleId(Long taxRuleId) { this.taxRuleId = taxRuleId; return this; }
        public SubscriptionLineBuilder lineTotalPaise(Long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; return this; }
        public SubscriptionLineBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SubscriptionLineBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public SubscriptionLine build() {
            return new SubscriptionLine(id, subscriptionId, productId, variantId, quantity, unitPricePaise, taxRuleId, lineTotalPaise, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getUnitPricePaise() { return unitPricePaise; }
    public void setUnitPricePaise(Long unitPricePaise) { this.unitPricePaise = unitPricePaise; }
    public Long getTaxRuleId() { return taxRuleId; }
    public void setTaxRuleId(Long taxRuleId) { this.taxRuleId = taxRuleId; }
    public Long getLineTotalPaise() { return lineTotalPaise; }
    public void setLineTotalPaise(Long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
