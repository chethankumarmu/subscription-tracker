package com.brightloop.subscription.plans.entity;

import com.brightloop.subscription.catalog.entity.Product;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String status = "DRAFT"; // DRAFT, PUBLISHED, RETIRED

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private PlanVersion currentVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Plan() {
    }

    public Plan(Long id, String code, String name, Product product, String status, PlanVersion currentVersion, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.product = product;
        this.status = status != null ? status : "DRAFT";
        this.currentVersion = currentVersion;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlanVersion getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(PlanVersion currentVersion) {
        this.currentVersion = currentVersion;
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
        private String code;
        private String name;
        private Product product;
        private String status = "DRAFT";
        private PlanVersion currentVersion;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder currentVersion(PlanVersion currentVersion) {
            this.currentVersion = currentVersion;
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

        public Plan build() {
            return new Plan(id, code, name, product, status, currentVersion, createdAt, updatedAt);
        }
    }
}
