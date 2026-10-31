package com.brightloop.subscription.discounts.dto;

import java.time.OffsetDateTime;

public class DiscountDto {
    private Long id;
    private String code;
    private String name;
    private String discountType;
    private Long value;
    private String scope;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private Integer usageLimit;
    private Boolean isActive;

    public DiscountDto() {}

    public DiscountDto(Long id, String code, String name, String discountType, Long value, String scope, OffsetDateTime startsAt, OffsetDateTime endsAt, Integer usageLimit, Boolean isActive) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        this.scope = scope;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.usageLimit = usageLimit;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDiscountType() { return discountType; }
    public Long getValue() { return value; }
    public String getScope() { return scope; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public Integer getUsageLimit() { return usageLimit; }
    public Boolean getIsActive() { return isActive; }
}

