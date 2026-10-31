package com.brightloop.subscription.plans.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_limits", uniqueConstraints = {
        @UniqueConstraint(name = "uq_plan_limits_version_key", columnNames = {"plan_version_id", "limit_key"})
})
public class PlanLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_version_id", nullable = false)
    private PlanVersion planVersion;

    @Column(name = "limit_key", nullable = false)
    private String limitKey; // USERS, STORAGE_GB, API_CALLS, CUSTOM_DOMAINS

    @Column(name = "limit_value")
    private Long limitValue; // NULL = unlimited

    public PlanLimit() {
    }

    public PlanLimit(Long id, PlanVersion planVersion, String limitKey, Long limitValue) {
        this.id = id;
        this.planVersion = planVersion;
        this.limitKey = limitKey;
        this.limitValue = limitValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanVersion getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(PlanVersion planVersion) {
        this.planVersion = planVersion;
    }

    public String getLimitKey() {
        return limitKey;
    }

    public void setLimitKey(String limitKey) {
        this.limitKey = limitKey;
    }

    public Long getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(Long limitValue) {
        this.limitValue = limitValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private PlanVersion planVersion;
        private String limitKey;
        private Long limitValue;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder planVersion(PlanVersion planVersion) {
            this.planVersion = planVersion;
            return this;
        }

        public Builder limitKey(String limitKey) {
            this.limitKey = limitKey;
            return this;
        }

        public Builder limitValue(Long limitValue) {
            this.limitValue = limitValue;
            return this;
        }

        public PlanLimit build() {
            return new PlanLimit(id, planVersion, limitKey, limitValue);
        }
    }
}
