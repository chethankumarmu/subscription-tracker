package com.brightloop.subscription.plans.dto;

import java.util.Map;

public class PublishedPlanDto {

    private Long id;
    private String code;
    private String name;
    private String status;
    private Long currentVersionId;
    private Integer versionNo;
    private Long pricePaise;
    private String formattedPrice;
    private String billingCycle;
    private Integer minQuantity;
    private Map<String, Long> limits;

    public PublishedPlanDto() {
    }

    public PublishedPlanDto(Long id, String code, String name, String status, Long currentVersionId, Integer versionNo, Long pricePaise, String formattedPrice, String billingCycle, Integer minQuantity, Map<String, Long> limits) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.currentVersionId = currentVersionId;
        this.versionNo = versionNo;
        this.pricePaise = pricePaise;
        this.formattedPrice = formattedPrice;
        this.billingCycle = billingCycle;
        this.minQuantity = minQuantity;
        this.limits = limits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurrentVersionId() {
        return currentVersionId;
    }

    public void setCurrentVersionId(Long currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
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

    public Map<String, Long> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Long> limits) {
        this.limits = limits;
    }
}
