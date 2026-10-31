package com.brightloop.subscription.discounts.dto;

import java.time.OffsetDateTime;

public class DiscountRequest {
    private String code;
    private String name;
    private String discountType;
    private Long value;
    private String scope;
    private Integer usageLimit;
    private OffsetDateTime endsAt;

    public DiscountRequest() {}

    public DiscountRequest(String code, String name, String discountType, Long value, String scope, Integer usageLimit, OffsetDateTime endsAt) {
        this.code = code;
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        this.scope = scope;
        this.usageLimit = usageLimit;
        this.endsAt = endsAt;
    }

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
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; }
}
