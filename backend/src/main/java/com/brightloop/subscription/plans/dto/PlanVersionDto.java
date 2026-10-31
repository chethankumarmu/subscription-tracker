package com.brightloop.subscription.plans.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;

public class PlanVersionDto {

    private Long id;
    private Long planId;
    private String planCode;
    private String planName;
    private Integer versionNo;
    private String status;
    private Long pricePaise;
    private String formattedPrice;
    private String billingCycle;
    private Integer minQuantity;
    private LocalDate availableFrom;
    private LocalDate availableTo;
    private Boolean autoClose;
    private Boolean allowEarlyClose;
    private Boolean allowPause;
    private Boolean allowRenewal;
    private OffsetDateTime effectiveFrom;
    private OffsetDateTime effectiveTo;
    private Map<String, Long> limits;
    private OffsetDateTime createdAt;

    public PlanVersionDto() {
    }

    public PlanVersionDto(Long id, Long planId, String planCode, String planName, Integer versionNo, String status, Long pricePaise, String formattedPrice, String billingCycle, Integer minQuantity, LocalDate availableFrom, LocalDate availableTo, Boolean autoClose, Boolean allowEarlyClose, Boolean allowPause, Boolean allowRenewal, OffsetDateTime effectiveFrom, OffsetDateTime effectiveTo, Map<String, Long> limits, OffsetDateTime createdAt) {
        this.id = id;
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
        this.versionNo = versionNo;
        this.status = status;
        this.pricePaise = pricePaise;
        this.formattedPrice = formattedPrice;
        this.billingCycle = billingCycle;
        this.minQuantity = minQuantity;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.autoClose = autoClose;
        this.allowEarlyClose = allowEarlyClose;
        this.allowPause = allowPause;
        this.allowRenewal = allowRenewal;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.limits = limits;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPricePaise() {
        return pricePaise;
    }

    public void setPricePaise(Long pricePaise) {
        this.pricePaise = pricePaise;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDate getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDate availableTo) {
        this.availableTo = availableTo;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public Boolean getAllowEarlyClose() {
        return allowEarlyClose;
    }

    public void setAllowEarlyClose(Boolean allowEarlyClose) {
        this.allowEarlyClose = allowEarlyClose;
    }

    public Boolean getAllowPause() {
        return allowPause;
    }

    public void setAllowPause(Boolean allowPause) {
        this.allowPause = allowPause;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(OffsetDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(OffsetDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Map<String, Long> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Long> limits) {
        this.limits = limits;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
