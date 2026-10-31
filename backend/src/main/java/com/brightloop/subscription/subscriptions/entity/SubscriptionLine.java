package com.brightloop.subscription.subscriptions.entity;

import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.entity.ProductVariant;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subscription_lines")
public class SubscriptionLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price_paise", nullable = false)
    private Long unitPricePaise;

    @Column(name = "tax_rule_id")
    private Long taxRuleId;

    @Column(name = "line_total_paise", nullable = false)
    private Long lineTotalPaise;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public SubscriptionLine() {
    }

    public SubscriptionLine(Long id, Subscription subscription, Product product, ProductVariant variant, Integer quantity, Long unitPricePaise, Long taxRuleId, Long lineTotalPaise, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.subscription = subscription;
        this.product = product;
        this.variant = variant;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPricePaise = unitPricePaise;
        this.taxRuleId = taxRuleId;
        this.lineTotalPaise = lineTotalPaise;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUnitPricePaise() {
        return unitPricePaise;
    }

    public void setUnitPricePaise(Long unitPricePaise) {
        this.unitPricePaise = unitPricePaise;
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
    }

    public Long getLineTotalPaise() {
        return lineTotalPaise;
    }

    public void setLineTotalPaise(Long lineTotalPaise) {
        this.lineTotalPaise = lineTotalPaise;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Subscription subscription;
        private Product product;
        private ProductVariant variant;
        private Integer quantity = 1;
        private Long unitPricePaise;
        private Long taxRuleId;
        private Long lineTotalPaise;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder subscription(Subscription subscription) {
            this.subscription = subscription;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder variant(ProductVariant variant) {
            this.variant = variant;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPricePaise(Long unitPricePaise) {
            this.unitPricePaise = unitPricePaise;
            return this;
        }

        public Builder taxRuleId(Long taxRuleId) {
            this.taxRuleId = taxRuleId;
            return this;
        }

        public Builder lineTotalPaise(Long lineTotalPaise) {
            this.lineTotalPaise = lineTotalPaise;
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

        public SubscriptionLine build() {
            return new SubscriptionLine(id, subscription, product, variant, quantity, unitPricePaise, taxRuleId, lineTotalPaise, createdAt, updatedAt);
        }
    }
}
