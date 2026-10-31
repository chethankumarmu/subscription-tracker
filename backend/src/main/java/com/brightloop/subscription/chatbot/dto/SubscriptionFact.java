package com.brightloop.subscription.chatbot.dto;

import java.time.LocalDate;

public class SubscriptionFact {
    private String number;
    private String status;
    private boolean paused;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private LocalDate nextBillingDate;
    private String planName;
    private String planCode;
    private Long pricePaise;
    private String priceFormatted;
    private String billingCycle;
    private boolean allowPause;
    private boolean allowRenewal;

    public SubscriptionFact() {}

    public SubscriptionFact(String number, String status, boolean paused, LocalDate startDate, LocalDate expiryDate, LocalDate nextBillingDate, String planName, String planCode, Long pricePaise, String priceFormatted, String billingCycle, boolean allowPause, boolean allowRenewal) {
        this.number = number;
        this.status = status;
        this.paused = paused;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.nextBillingDate = nextBillingDate;
        this.planName = planName;
        this.planCode = planCode;
        this.pricePaise = pricePaise;
        this.priceFormatted = priceFormatted;
        this.billingCycle = billingCycle;
        this.allowPause = allowPause;
        this.allowRenewal = allowRenewal;
    }

    public static SubscriptionFactBuilder builder() {
        return new SubscriptionFactBuilder();
    }

    public static class SubscriptionFactBuilder {
        private String number;
        private String status;
        private boolean paused;
        private LocalDate startDate;
        private LocalDate expiryDate;
        private LocalDate nextBillingDate;
        private String planName;
        private String planCode;
        private Long pricePaise;
        private String priceFormatted;
        private String billingCycle;
        private boolean allowPause;
        private boolean allowRenewal;

        public SubscriptionFactBuilder number(String number) { this.number = number; return this; }
        public SubscriptionFactBuilder status(String status) { this.status = status; return this; }
        public SubscriptionFactBuilder paused(boolean paused) { this.paused = paused; return this; }
        public SubscriptionFactBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public SubscriptionFactBuilder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public SubscriptionFactBuilder nextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; return this; }
        public SubscriptionFactBuilder planName(String planName) { this.planName = planName; return this; }
        public SubscriptionFactBuilder planCode(String planCode) { this.planCode = planCode; return this; }
        public SubscriptionFactBuilder pricePaise(Long pricePaise) { this.pricePaise = pricePaise; return this; }
        public SubscriptionFactBuilder priceFormatted(String priceFormatted) { this.priceFormatted = priceFormatted; return this; }
        public SubscriptionFactBuilder billingCycle(String billingCycle) { this.billingCycle = billingCycle; return this; }
        public SubscriptionFactBuilder allowPause(boolean allowPause) { this.allowPause = allowPause; return this; }
        public SubscriptionFactBuilder allowRenewal(boolean allowRenewal) { this.allowRenewal = allowRenewal; return this; }

        public SubscriptionFact build() {
            return new SubscriptionFact(number, status, paused, startDate, expiryDate, nextBillingDate, planName, planCode, pricePaise, priceFormatted, billingCycle, allowPause, allowRenewal);
        }
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    public Long getPricePaise() { return pricePaise; }
    public void setPricePaise(Long pricePaise) { this.pricePaise = pricePaise; }
    public String getPriceFormatted() { return priceFormatted; }
    public void setPriceFormatted(String priceFormatted) { this.priceFormatted = priceFormatted; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public boolean isAllowPause() { return allowPause; }
    public void setAllowPause(boolean allowPause) { this.allowPause = allowPause; }
    public boolean isAllowRenewal() { return allowRenewal; }
    public void setAllowRenewal(boolean allowRenewal) { this.allowRenewal = allowRenewal; }
}
