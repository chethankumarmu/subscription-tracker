package com.brightloop.subscription.plans.dto;

import jakarta.validation.constraints.NotBlank;

public class PlanLimitDto {

    @NotBlank(message = "limitKey is required")
    private String limitKey; // USERS, STORAGE_GB, API_CALLS, CUSTOM_DOMAINS

    private Long limitValue; // null = unlimited

    public PlanLimitDto() {
    }

    public PlanLimitDto(String limitKey, Long limitValue) {
        this.limitKey = limitKey;
        this.limitValue = limitValue;
    }

    public String getLimitKey() {
        return limitKey;
    }

    public void setLimitKey(String limitKey) {
        this.limitKey = limitKey;
    }

    public Long getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Long limitValue) {
        this.limitValue = limitValue;
    }
}
