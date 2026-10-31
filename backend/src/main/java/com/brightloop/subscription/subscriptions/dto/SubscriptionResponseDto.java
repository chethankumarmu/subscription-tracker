package com.brightloop.subscription.subscriptions.dto;

import java.time.LocalDate;

public class SubscriptionResponseDto {

    private Long id;
    private String number;
    private Long customerId;
    private String customerName;
    private Long planVersionId;
    private String planCode;
    private Integer versionNo;
    private String status;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private LocalDate nextBillingDate;
    private Long totalPaise;
    private String formattedTotal;

    public SubscriptionResponseDto() {
    }

    public SubscriptionResponseDto(Long id, String number, Long customerId, String customerName, Long planVersionId, String planCode, Integer versionNo, String status, LocalDate startDate, LocalDate expiryDate, LocalDate nextBillingDate, Long totalPaise, String formattedTotal) {
        this.id = id;
        this.number = number;
        this.customerId = customerId;
        this.customerName = customerName;
        this.planVersionId = planVersionId;
        this.planCode = planCode;
        this.versionNo = versionNo;
        this.status = status;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.nextBillingDate = nextBillingDate;
        this.totalPaise = totalPaise;
        this.formattedTotal = formattedTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(Long planVersionId) {
        this.planVersionId = planVersionId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public Long getTotalPaise() {
        return totalPaise;
    }

    public void setTotalPaise(Long totalPaise) {
        this.totalPaise = totalPaise;
    }

    public String getFormattedTotal() {
        return formattedTotal;
    }

    public void setFormattedTotal(String formattedTotal) {
        this.formattedTotal = formattedTotal;
    }
}
