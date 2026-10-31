package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "variant_id")
    private Long variantId;

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

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public InvoiceLine() {}

    public InvoiceLine(Long id, Long invoiceId, Long productId, Long variantId, String description, Integer quantity, Long unitPricePaise, Long discountPaise, Long taxRuleId, Integer taxRateBps, Long taxPaise, Long lineTotalPaise, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.variantId = variantId;
        this.description = description;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPricePaise = unitPricePaise;
        this.discountPaise = discountPaise != null ? discountPaise : 0L;
        this.taxRuleId = taxRuleId;
        this.taxRateBps = taxRateBps != null ? taxRateBps : 0;
        this.taxPaise = taxPaise != null ? taxPaise : 0L;
        this.lineTotalPaise = lineTotalPaise;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static InvoiceLineBuilder builder() {
        return new InvoiceLineBuilder();
    }

    public static class InvoiceLineBuilder {
        private Long id;
        private Long invoiceId;
        private Long productId;
        private Long variantId;
        private String description;
        private Integer quantity = 1;
        private Long unitPricePaise;
        private Long discountPaise = 0L;
        private Long taxRuleId;
        private Integer taxRateBps = 0;
        private Long taxPaise = 0L;
        private Long lineTotalPaise;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public InvoiceLineBuilder id(Long id) { this.id = id; return this; }
        public InvoiceLineBuilder invoiceId(Long invoiceId) { this.invoiceId = invoiceId; return this; }
        public InvoiceLineBuilder productId(Long productId) { this.productId = productId; return this; }
        public InvoiceLineBuilder variantId(Long variantId) { this.variantId = variantId; return this; }
        public InvoiceLineBuilder description(String description) { this.description = description; return this; }
        public InvoiceLineBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public InvoiceLineBuilder unitPricePaise(Long unitPricePaise) { this.unitPricePaise = unitPricePaise; return this; }
        public InvoiceLineBuilder discountPaise(Long discountPaise) { this.discountPaise = discountPaise; return this; }
        public InvoiceLineBuilder taxRuleId(Long taxRuleId) { this.taxRuleId = taxRuleId; return this; }
        public InvoiceLineBuilder taxRateBps(Integer taxRateBps) { this.taxRateBps = taxRateBps; return this; }
        public InvoiceLineBuilder taxPaise(Long taxPaise) { this.taxPaise = taxPaise; return this; }
        public InvoiceLineBuilder lineTotalPaise(Long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; return this; }
        public InvoiceLineBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public InvoiceLineBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public InvoiceLine build() {
            return new InvoiceLine(id, invoiceId, productId, variantId, description, quantity, unitPricePaise, discountPaise, taxRuleId, taxRateBps, taxPaise, lineTotalPaise, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Long getUnitPricePaise() { return unitPricePaise; }
    public void setUnitPricePaise(Long unitPricePaise) { this.unitPricePaise = unitPricePaise; }
    public Long getDiscountPaise() { return discountPaise; }
    public void setDiscountPaise(Long discountPaise) { this.discountPaise = discountPaise; }
    public Long getTaxRuleId() { return taxRuleId; }
    public void setTaxRuleId(Long taxRuleId) { this.taxRuleId = taxRuleId; }
    public Integer getTaxRateBps() { return taxRateBps; }
    public void setTaxRateBps(Integer taxRateBps) { this.taxRateBps = taxRateBps; }
    public Long getTaxPaise() { return taxPaise; }
    public void setTaxPaise(Long taxPaise) { this.taxPaise = taxPaise; }
    public Long getLineTotalPaise() { return lineTotalPaise; }
    public void setLineTotalPaise(Long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

