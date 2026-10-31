package com.brightloop.subscription.dto;

import com.brightloop.subscription.model.PlanLimit;
import java.util.List;
import java.util.Map;

public class PlanDto {
    private Long id;
    private String code;
    private String name;
    private String status;
    private Long currentVersionId;
    private Long monthlyPricePaise;
    private Long yearlyPricePaise;
    private String billingCycle;
    private Boolean allowPause;
    private List<PlanLimit> limits;
    private Map<String, Object> featureMap;

    public PlanDto() {}

    public PlanDto(Long id, String code, String name, String status, Long currentVersionId, Long monthlyPricePaise, Long yearlyPricePaise, String billingCycle, Boolean allowPause, List<PlanLimit> limits, Map<String, Object> featureMap) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.currentVersionId = currentVersionId;
        this.monthlyPricePaise = monthlyPricePaise;
        this.yearlyPricePaise = yearlyPricePaise;
        this.billingCycle = billingCycle;
        this.allowPause = allowPause;
        this.limits = limits;
        this.featureMap = featureMap;
    }

    public static PlanDtoBuilder builder() {
        return new PlanDtoBuilder();
    }

    public static class PlanDtoBuilder {
        private Long id;
        private String code;
        private String name;
        private String status;
        private Long currentVersionId;
        private Long monthlyPricePaise;
        private Long yearlyPricePaise;
        private String billingCycle;
        private Boolean allowPause;
        private List<PlanLimit> limits;
        private Map<String, Object> featureMap;

        public PlanDtoBuilder id(Long id) { this.id = id; return this; }
        public PlanDtoBuilder code(String code) { this.code = code; return this; }
        public PlanDtoBuilder name(String name) { this.name = name; return this; }
        public PlanDtoBuilder status(String status) { this.status = status; return this; }
        public PlanDtoBuilder currentVersionId(Long currentVersionId) { this.currentVersionId = currentVersionId; return this; }
        public PlanDtoBuilder monthlyPricePaise(Long monthlyPricePaise) { this.monthlyPricePaise = monthlyPricePaise; return this; }
        public PlanDtoBuilder yearlyPricePaise(Long yearlyPricePaise) { this.yearlyPricePaise = yearlyPricePaise; return this; }
        public PlanDtoBuilder billingCycle(String billingCycle) { this.billingCycle = billingCycle; return this; }
        public PlanDtoBuilder allowPause(Boolean allowPause) { this.allowPause = allowPause; return this; }
        public PlanDtoBuilder limits(List<PlanLimit> limits) { this.limits = limits; return this; }
        public PlanDtoBuilder featureMap(Map<String, Object> featureMap) { this.featureMap = featureMap; return this; }

        public PlanDto build() {
            return new PlanDto(id, code, name, status, currentVersionId, monthlyPricePaise, yearlyPricePaise, billingCycle, allowPause, limits, featureMap);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCurrentVersionId() { return currentVersionId; }
    public void setCurrentVersionId(Long currentVersionId) { this.currentVersionId = currentVersionId; }
    public Long getMonthlyPricePaise() { return monthlyPricePaise; }
    public void setMonthlyPricePaise(Long monthlyPricePaise) { this.monthlyPricePaise = monthlyPricePaise; }
    public Long getYearlyPricePaise() { return yearlyPricePaise; }
    public void setYearlyPricePaise(Long yearlyPricePaise) { this.yearlyPricePaise = yearlyPricePaise; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public Boolean getAllowPause() { return allowPause; }
    public void setAllowPause(Boolean allowPause) { this.allowPause = allowPause; }
    public List<PlanLimit> getLimits() { return limits; }
    public void setLimits(List<PlanLimit> limits) { this.limits = limits; }
    public Map<String, Object> getFeatureMap() { return featureMap; }
    public void setFeatureMap(Map<String, Object> featureMap) { this.featureMap = featureMap; }
}
