package com.brightloop.subscription.dto;

public class SubscriptionRequest {
    private Long customerId;
    private Long planId;
    private String billingCycle;
    private String discountCode;

    public SubscriptionRequest() {}

    public SubscriptionRequest(Long customerId, Long planId, String billingCycle, String discountCode) {
        this.customerId = customerId;
        this.planId = planId;
        this.billingCycle = billingCycle;
        this.discountCode = discountCode;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
}
