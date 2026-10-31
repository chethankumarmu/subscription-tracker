package com.brightloop.subscription.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_limits")
public class PlanLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_version_id", nullable = false)
    private Long planVersionId;

    @Column(name = "limit_key", nullable = false, length = 100)
    private String limitKey;

    @Column(name = "limit_value")
    private Long limitValue;

    public PlanLimit() {}

    public PlanLimit(Long id, Long planVersionId, String limitKey, Long limitValue) {
        this.id = id;
        this.planVersionId = planVersionId;
        this.limitKey = limitKey;
        this.limitValue = limitValue;
    }

    public static PlanLimitBuilder builder() {
        return new PlanLimitBuilder();
    }

    public static class PlanLimitBuilder {
        private Long id;
        private Long planVersionId;
        private String limitKey;
        private Long limitValue;

        public PlanLimitBuilder id(Long id) { this.id = id; return this; }
        public PlanLimitBuilder planVersionId(Long planVersionId) { this.planVersionId = planVersionId; return this; }
        public PlanLimitBuilder limitKey(String limitKey) { this.limitKey = limitKey; return this; }
        public PlanLimitBuilder limitValue(Long limitValue) { this.limitValue = limitValue; return this; }

        public PlanLimit build() {
            return new PlanLimit(id, planVersionId, limitKey, limitValue);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanVersionId() { return planVersionId; }
    public void setPlanVersionId(Long planVersionId) { this.planVersionId = planVersionId; }
    public String getLimitKey() { return limitKey; }
    public void setLimitKey(String limitKey) { this.limitKey = limitKey; }
    public Long getLimitValue() { return limitValue; }
    public void setLimitValue(Long limitValue) { this.limitValue = limitValue; }
}

