package com.brightloop.subscription.reporting.dto;

import java.time.LocalDate;
import java.util.Map;

public class CustomerDashboardStatsDto {
    private String currentPlanName;
    private String currentPlanCode;
    private String billingCycle;
    private Long pricePaise;
    private String status;
    private LocalDate nextRenewalDate;
    private Long amountDuePaise;
    private Map<String, Long> limits;

    public CustomerDashboardStatsDto() {}

    public CustomerDashboardStatsDto(String currentPlanName, String currentPlanCode, String billingCycle, Long pricePaise, String status, LocalDate nextRenewalDate, Long amountDuePaise, Map<String, Long> limits) {
        this.currentPlanName = currentPlanName;
        this.currentPlanCode = currentPlanCode;
        this.billingCycle = billingCycle;
        this.pricePaise = pricePaise;
        this.status = status;
        this.nextRenewalDate = nextRenewalDate;
        this.amountDuePaise = amountDuePaise;
        this.limits = limits;
    }

    public String getCurrentPlanName() { return currentPlanName; }
    public String getCurrentPlanCode() { return currentPlanCode; }
    public String getBillingCycle() { return billingCycle; }
    public Long getPricePaise() { return pricePaise; }
    public String getStatus() { return status; }
    public LocalDate getNextRenewalDate() { return nextRenewalDate; }
    public Long getAmountDuePaise() { return amountDuePaise; }
    public Map<String, Long> getLimits() { return limits; }
}

