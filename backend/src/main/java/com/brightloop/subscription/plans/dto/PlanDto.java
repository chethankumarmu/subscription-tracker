package com.brightloop.subscription.plans.dto;

import java.time.OffsetDateTime;

public class PlanDto {

    private Long id;
    private String code;
    private String name;
    private Long productId;
    private String productName;
    private String status;
    private PlanVersionDto currentVersion;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public PlanDto() {
    }

    public PlanDto(Long id, String code, String name, Long productId, String productName, String status, PlanVersionDto currentVersion, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.productId = productId;
        this.productName = productName;
        this.status = status;
        this.currentVersion = currentVersion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PlanVersionDto getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(PlanVersionDto currentVersion) {
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
}
