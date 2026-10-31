package com.brightloop.subscription.subscriptions.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateSubscriptionRequest {

    private Long customerId;

    @NotNull(message = "planVersionId is required")
    private Long planVersionId;

    private Integer quantity = 1;

    private LocalDate startDate;

    private Integer paymentTermsDays = 30;

    public CreateSubscriptionRequest() {
    }

    public CreateSubscriptionRequest(Long customerId, Long planVersionId, Integer quantity, LocalDate startDate, Integer paymentTermsDays) {
        this.customerId = customerId;
        this.planVersionId = planVersionId;
        this.quantity = quantity != null ? quantity : 1;
        this.startDate = startDate;
        this.paymentTermsDays = paymentTermsDays != null ? paymentTermsDays : 30;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(Long planVersionId) {
        this.planVersionId = planVersionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }
}
