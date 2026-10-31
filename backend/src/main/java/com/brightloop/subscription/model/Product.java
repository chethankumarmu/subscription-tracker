package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    @Column(name = "sales_price_paise", nullable = false)
    private Long salesPricePaise;

    @Column(name = "cost_price_paise", nullable = false)
    private Long costPricePaise = 0L;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Product() {}

    public Product(Long id, String sku, String name, String productType, Long salesPricePaise, Long costPricePaise, String notes, Boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.productType = productType;
        this.salesPricePaise = salesPricePaise;
        this.costPricePaise = costPricePaise != null ? costPricePaise : 0L;
        this.notes = notes;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private Long id;
        private String sku;
        private String name;
        private String productType;
        private Long salesPricePaise;
        private Long costPricePaise = 0L;
        private String notes;
        private Boolean isActive = true;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public ProductBuilder id(Long id) { this.id = id; return this; }
        public ProductBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductBuilder name(String name) { this.name = name; return this; }
        public ProductBuilder productType(String productType) { this.productType = productType; return this; }
        public ProductBuilder salesPricePaise(Long salesPricePaise) { this.salesPricePaise = salesPricePaise; return this; }
        public ProductBuilder costPricePaise(Long costPricePaise) { this.costPricePaise = costPricePaise; return this; }
        public ProductBuilder notes(String notes) { this.notes = notes; return this; }
        public ProductBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public ProductBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProductBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Product build() {
            return new Product(id, sku, name, productType, salesPricePaise, costPricePaise, notes, isActive, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public Long getSalesPricePaise() { return salesPricePaise; }
    public void setSalesPricePaise(Long salesPricePaise) { this.salesPricePaise = salesPricePaise; }
    public Long getCostPricePaise() { return costPricePaise; }
    public void setCostPricePaise(Long costPricePaise) { this.costPricePaise = costPricePaise; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
