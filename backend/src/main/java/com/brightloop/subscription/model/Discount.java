package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "discount_type", nullable = false, length = 50)
    private String discountType;

    @Column(nullable = false)
    private Long value;

    @Column(nullable = false, length = 50)
    private String scope;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "min_amount_paise")
    private Long minAmountPaise;

    @Column(name = "min_quantity")
    private Integer minQuantity;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at")
    private OffsetDateTime endsAt;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Discount() {}

    public Discount(Long id, String code, String name, String discountType, Long value, String scope, Long productId, Long minAmountPaise, Integer minQuantity, OffsetDateTime startsAt, OffsetDateTime endsAt, Integer usageLimit, Boolean isActive, Integer version, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        this.scope = scope;
        this.productId = productId;
        this.minAmountPaise = minAmountPaise;
        this.minQuantity = minQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.usageLimit = usageLimit;
        this.isActive = isActive != null ? isActive : true;
        this.version = version != null ? version : 0;
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static DiscountBuilder builder() {
        return new DiscountBuilder();
    }

    public static class DiscountBuilder {
        private Long id;
        private String code;
        private String name;
        private String discountType;
        private Long value;
        private String scope;
        private Long productId;
        private Long minAmountPaise;
        private Integer minQuantity;
        private OffsetDateTime startsAt;
        private OffsetDateTime endsAt;
        private Integer usageLimit;
        private Boolean isActive = true;
        private Integer version = 0;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public DiscountBuilder id(Long id) { this.id = id; return this; }
        public DiscountBuilder code(String code) { this.code = code; return this; }
        public DiscountBuilder name(String name) { this.name = name; return this; }
        public DiscountBuilder discountType(String discountType) { this.discountType = discountType; return this; }
        public DiscountBuilder value(Long value) { this.value = value; return this; }
        public DiscountBuilder scope(String scope) { this.scope = scope; return this; }
        public DiscountBuilder productId(Long productId) { this.productId = productId; return this; }
        public DiscountBuilder minAmountPaise(Long minAmountPaise) { this.minAmountPaise = minAmountPaise; return this; }
        public DiscountBuilder minQuantity(Integer minQuantity) { this.minQuantity = minQuantity; return this; }
        public DiscountBuilder startsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; return this; }
        public DiscountBuilder endsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; return this; }
        public DiscountBuilder usageLimit(Integer usageLimit) { this.usageLimit = usageLimit; return this; }
        public DiscountBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public DiscountBuilder version(Integer version) { this.version = version; return this; }
        public DiscountBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DiscountBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Discount build() {
            return new Discount(id, code, name, discountType, value, scope, productId, minAmountPaise, minQuantity, startsAt, endsAt, usageLimit, isActive, version, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getMinAmountPaise() { return minAmountPaise; }
    public void setMinAmountPaise(Long minAmountPaise) { this.minAmountPaise = minAmountPaise; }
    public Integer getMinQuantity() { return minQuantity; }
    public void setMinQuantity(Integer minQuantity) { this.minQuantity = minQuantity; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public void setStartsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

