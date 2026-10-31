package com.brightloop.subscription.plans.entity;

import com.brightloop.subscription.auth.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "plan_versions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_plan_versions_plan_version_no", columnNames = {"plan_id", "version_no"})
})
public class PlanVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "price_paise", nullable = false)
    private Long pricePaise;

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle; // DAILY, WEEKLY, MONTHLY, YEARLY

    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity = 1;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    @Column(name = "available_to")
    private LocalDate availableTo;

    @Column(name = "auto_close", nullable = false)
    private Boolean autoClose = false;

    @Column(name = "allow_early_close", nullable = false)
    private Boolean allowEarlyClose = false;

    @Column(name = "allow_pause", nullable = false)
    private Boolean allowPause = false;

    @Column(name = "allow_renewal", nullable = false)
    private Boolean allowRenewal = true;

    @Column(name = "effective_from", nullable = false)
    private OffsetDateTime effectiveFrom = OffsetDateTime.now();

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public PlanVersion() {
    }

    public PlanVersion(Long id, Plan plan, Integer versionNo, Long pricePaise, String billingCycle, Integer minQuantity, LocalDate availableFrom, LocalDate availableTo, Boolean autoClose, Boolean allowEarlyClose, Boolean allowPause, Boolean allowRenewal, OffsetDateTime effectiveFrom, OffsetDateTime effectiveTo, User createdBy, OffsetDateTime createdAt) {
        this.id = id;
        this.plan = plan;
        this.versionNo = versionNo;
        this.pricePaise = pricePaise;
        this.billingCycle = billingCycle;
        this.minQuantity = minQuantity != null ? minQuantity : 1;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.autoClose = autoClose != null ? autoClose : false;
        this.allowEarlyClose = allowEarlyClose != null ? allowEarlyClose : false;
        this.allowPause = allowPause != null ? allowPause : false;
        this.allowRenewal = allowRenewal != null ? allowRenewal : true;
        this.effectiveFrom = effectiveFrom != null ? effectiveFrom : OffsetDateTime.now();
        this.effectiveTo = effectiveTo;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (effectiveFrom == null) {
            effectiveFrom = OffsetDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
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

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(OffsetDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(OffsetDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Plan plan;
        private Integer versionNo;
        private Long pricePaise;
        private String billingCycle;
        private Integer minQuantity = 1;
        private LocalDate availableFrom;
        private LocalDate availableTo;
        private Boolean autoClose = false;
        private Boolean allowEarlyClose = false;
        private Boolean allowPause = false;
        private Boolean allowRenewal = true;
        private OffsetDateTime effectiveFrom;
        private OffsetDateTime effectiveTo;
        private User createdBy;
        private OffsetDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder plan(Plan plan) {
            this.plan = plan;
            return this;
        }

        public Builder versionNo(Integer versionNo) {
            this.versionNo = versionNo;
            return this;
        }

        public Builder pricePaise(Long pricePaise) {
            this.pricePaise = pricePaise;
            return this;
        }

        public Builder billingCycle(String billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public Builder minQuantity(Integer minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }

        public Builder availableFrom(LocalDate availableFrom) {
            this.availableFrom = availableFrom;
            return this;
        }

        public Builder availableTo(LocalDate availableTo) {
            this.availableTo = availableTo;
            return this;
        }

        public Builder autoClose(Boolean autoClose) {
            this.autoClose = autoClose;
            return this;
        }

        public Builder allowEarlyClose(Boolean allowEarlyClose) {
            this.allowEarlyClose = allowEarlyClose;
            return this;
        }

        public Builder allowPause(Boolean allowPause) {
            this.allowPause = allowPause;
            return this;
        }

        public Builder allowRenewal(Boolean allowRenewal) {
            this.allowRenewal = allowRenewal;
            return this;
        }

        public Builder effectiveFrom(OffsetDateTime effectiveFrom) {
            this.effectiveFrom = effectiveFrom;
            return this;
        }

        public Builder effectiveTo(OffsetDateTime effectiveTo) {
            this.effectiveTo = effectiveTo;
            return this;
        }

        public Builder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PlanVersion build() {
            return new PlanVersion(id, plan, versionNo, pricePaise, billingCycle, minQuantity, availableFrom, availableTo, autoClose, allowEarlyClose, allowPause, allowRenewal, effectiveFrom, effectiveTo, createdBy, createdAt);
        }
    }
}
