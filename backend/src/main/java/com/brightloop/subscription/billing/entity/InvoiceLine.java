package com.brightloop.subscription.billing.entity;

import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.entity.ProductVariant;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price_paise", nullable = false)
    private Long unitPricePaise;

    @Column(name = "discount_paise", nullable = false)
    private Long discountPaise = 0L;

    @Column(name = "tax_rule_id")
    private Long taxRuleId;

    @Column(name = "tax_rate_bps", nullable = false)
    private Integer taxRateBps = 0;

    @Column(name = "tax_paise", nullable = false)
    private Long taxPaise = 0L;

    @Column(name = "line_total_paise", nullable = false)
    private Long lineTotalPaise;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public InvoiceLine() {
    }

    public InvoiceLine(Long id, Invoice invoice, Product product, ProductVariant variant, String description, Integer quantity, Long unitPricePaise, Long discountPaise, Long taxRuleId, Integer taxRateBps, Long taxPaise, Long lineTotalPaise, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.invoice = invoice;
        this.product = product;
        this.variant = variant;
        this.description = description;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPricePaise = unitPricePaise;
        this.discountPaise = discountPaise != null ? discountPaise : 0L;
        this.taxRuleId = taxRuleId;
        this.taxRateBps = taxRateBps != null ? taxRateBps : 0;
        this.taxPaise = taxPaise != null ? taxPaise : 0L;
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getDiscountPaise() {
        return discountPaise;
    }

    public void setDiscountPaise(Long discountPaise) {
        this.discountPaise = discountPaise;
    }

    public Long getTaxRuleId() {
        return taxRuleId;
    }

    public void setTaxRuleId(Long taxRuleId) {
        this.taxRuleId = taxRuleId;
    }

    public Integer getTaxRateBps() {
        return taxRateBps;
    }

    public void setTaxRateBps(Integer taxRateBps) {
        this.taxRateBps = taxRateBps;
    }

    public Long getTaxPaise() {
        return taxPaise;
    }

    public void setTaxPaise(Long taxPaise) {
        this.taxPaise = taxPaise;
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
        private Invoice invoice;
        private Product product;
        private ProductVariant variant;
        private String description;
        private Integer quantity = 1;
        private Long unitPricePaise;
        private Long discountPaise = 0L;
        private Long taxRuleId;
        private Integer taxRateBps = 0;
        private Long taxPaise = 0L;
        private Long lineTotalPaise;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder invoice(Invoice invoice) {
            this.invoice = invoice;
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

        public Builder description(String description) {
            this.description = description;
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

        public Builder discountPaise(Long discountPaise) {
            this.discountPaise = discountPaise;
            return this;
        }

        public Builder taxRuleId(Long taxRuleId) {
            this.taxRuleId = taxRuleId;
            return this;
        }

        public Builder taxRateBps(Integer taxRateBps) {
            this.taxRateBps = taxRateBps;
            return this;
        }

        public Builder taxPaise(Long taxPaise) {
            this.taxPaise = taxPaise;
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

        public InvoiceLine build() {
            return new InvoiceLine(id, invoice, product, variant, description, quantity, unitPricePaise, discountPaise, taxRuleId, taxRateBps, taxPaise, lineTotalPaise, createdAt, updatedAt);
        }
    }
}
