package com.brightloop.subscription.catalog.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "product_variants", uniqueConstraints = {
        @UniqueConstraint(name = "uq_product_variant_product_name", columnNames = {"product_id", "name"})
})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String attribute;

    @Column(name = "price_adder_paise", nullable = false)
    private Long priceAdderPaise = 0L;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ProductVariant() {
    }

    public ProductVariant(Long id, Product product, String name, String attribute, Long priceAdderPaise, Boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.name = name;
        this.attribute = attribute;
        this.priceAdderPaise = priceAdderPaise != null ? priceAdderPaise : 0L;
        this.isActive = isActive != null ? isActive : true;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Long getPriceAdderPaise() {
        return priceAdderPaise;
    }

    public void setPriceAdderPaise(Long priceAdderPaise) {
        this.priceAdderPaise = priceAdderPaise;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        private Product product;
        private String name;
        private String attribute;
        private Long priceAdderPaise = 0L;
        private Boolean isActive = true;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder priceAdderPaise(Long priceAdderPaise) {
            this.priceAdderPaise = priceAdderPaise;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
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

        public ProductVariant build() {
            return new ProductVariant(id, product, name, attribute, priceAdderPaise, isActive, createdAt, updatedAt);
        }
    }
}
