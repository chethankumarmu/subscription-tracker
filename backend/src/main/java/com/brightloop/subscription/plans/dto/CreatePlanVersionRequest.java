package com.brightloop.subscription.plans.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreatePlanVersionRequest {

    @NotNull(message = "pricePaise is required")
    private Long pricePaise;

    @NotNull(message = "billingCycle is required")
    private String billingCycle;

    private Integer minQuantity = 1;

    @NotNull(message = "availableFrom is required")
    private LocalDate availableFrom;

    private LocalDate availableTo;

    private Boolean autoClose = false;

    private Boolean allowEarlyClose = false;

    private Boolean allowPause = false;

    private Boolean allowRenewal = true;

    private List<PlanLimitDto> limits = new ArrayList<>();

    public CreatePlanVersionRequest() {
    }

    public CreatePlanVersionRequest(Long pricePaise, String billingCycle, Integer minQuantity, LocalDate availableFrom, LocalDate availableTo, Boolean autoClose, Boolean allowEarlyClose, Boolean allowPause, Boolean allowRenewal, List<PlanLimitDto> limits) {
        this.pricePaise = pricePaise;
        this.billingCycle = billingCycle;
        this.minQuantity = minQuantity != null ? minQuantity : 1;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.autoClose = autoClose != null ? autoClose : false;
        this.allowEarlyClose = allowEarlyClose != null ? allowEarlyClose : false;
        this.allowPause = allowPause != null ? allowPause : false;
        this.allowRenewal = allowRenewal != null ? allowRenewal : true;
        this.limits = limits != null ? limits : new ArrayList<>();
    }

    public Long getPricePaise() {
        return pricePaise;
    }

    public void setPricePaise(Long pricePaise) {
        this.pricePaise = pricePaise;
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

    public List<PlanLimitDto> getLimits() {
        return limits;
    }

    public void setLimits(List<PlanLimitDto> limits) {
        this.limits = limits;
    }
}
