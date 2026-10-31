package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 50)
    private String status = "DRAFT";

    @Column(name = "current_version_id")
    private Long currentVersionId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Plan() {}

    public Plan(Long id, String code, String name, Long productId, String status, Long currentVersionId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.productId = productId;
        this.status = status != null ? status : "DRAFT";
        this.currentVersionId = currentVersionId;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    public static class PlanBuilder {
        private Long id;
        private String code;
        private String name;
        private Long productId;
        private String status = "DRAFT";
        private Long currentVersionId;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public PlanBuilder id(Long id) { this.id = id; return this; }
        public PlanBuilder code(String code) { this.code = code; return this; }
        public PlanBuilder name(String name) { this.name = name; return this; }
        public PlanBuilder productId(Long productId) { this.productId = productId; return this; }
        public PlanBuilder status(String status) { this.status = status; return this; }
        public PlanBuilder currentVersionId(Long currentVersionId) { this.currentVersionId = currentVersionId; return this; }
        public PlanBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PlanBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Plan build() {
            return new Plan(id, code, name, productId, status, currentVersionId, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCurrentVersionId() { return currentVersionId; }
    public void setCurrentVersionId(Long currentVersionId) { this.currentVersionId = currentVersionId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
