package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "plan_versions")
public class PlanVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "price_paise", nullable = false)
    private Long pricePaise;

    @Column(name = "billing_cycle", nullable = false, length = 50)
    private String billingCycle;

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

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public PlanVersion() {}

    public PlanVersion(Long id, Long planId, Integer versionNo, Long pricePaise, String billingCycle, Integer minQuantity, LocalDate availableFrom, LocalDate availableTo, Boolean autoClose, Boolean allowEarlyClose, Boolean allowPause, Boolean allowRenewal, OffsetDateTime effectiveFrom, OffsetDateTime effectiveTo, Long createdBy, OffsetDateTime createdAt) {
        this.id = id;
        this.planId = planId;
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
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
    }

    public static PlanVersionBuilder builder() {
        return new PlanVersionBuilder();
    }

    public static class PlanVersionBuilder {
        private Long id;
        private Long planId;
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
        private OffsetDateTime effectiveFrom = OffsetDateTime.now();
        private OffsetDateTime effectiveTo;
        private Long createdBy;
        private OffsetDateTime createdAt = OffsetDateTime.now();

        public PlanVersionBuilder id(Long id) { this.id = id; return this; }
        public PlanVersionBuilder planId(Long planId) { this.planId = planId; return this; }
        public PlanVersionBuilder versionNo(Integer versionNo) { this.versionNo = versionNo; return this; }
        public PlanVersionBuilder pricePaise(Long pricePaise) { this.pricePaise = pricePaise; return this; }
        public PlanVersionBuilder billingCycle(String billingCycle) { this.billingCycle = billingCycle; return this; }
        public PlanVersionBuilder minQuantity(Integer minQuantity) { this.minQuantity = minQuantity; return this; }
        public PlanVersionBuilder availableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; return this; }
        public PlanVersionBuilder availableTo(LocalDate availableTo) { this.availableTo = availableTo; return this; }
        public PlanVersionBuilder autoClose(Boolean autoClose) { this.autoClose = autoClose; return this; }
        public PlanVersionBuilder allowEarlyClose(Boolean allowEarlyClose) { this.allowEarlyClose = allowEarlyClose; return this; }
        public PlanVersionBuilder allowPause(Boolean allowPause) { this.allowPause = allowPause; return this; }
        public PlanVersionBuilder allowRenewal(Boolean allowRenewal) { this.allowRenewal = allowRenewal; return this; }
        public PlanVersionBuilder effectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; return this; }
        public PlanVersionBuilder effectiveTo(OffsetDateTime effectiveTo) { this.effectiveTo = effectiveTo; return this; }
        public PlanVersionBuilder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }
        public PlanVersionBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PlanVersion build() {
            return new PlanVersion(id, planId, versionNo, pricePaise, billingCycle, minQuantity, availableFrom, availableTo, autoClose, allowEarlyClose, allowPause, allowRenewal, effectiveFrom, effectiveTo, createdBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public Long getPricePaise() { return pricePaise; }
    public void setPricePaise(Long pricePaise) { this.pricePaise = pricePaise; }
    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }
    public Integer getMinQuantity() { return minQuantity; }
    public void setMinQuantity(Integer minQuantity) { this.minQuantity = minQuantity; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public LocalDate getAvailableTo() { return availableTo; }
    public void setAvailableTo(LocalDate availableTo) { this.availableTo = availableTo; }
    public Boolean getAutoClose() { return autoClose; }
    public void setAutoClose(Boolean autoClose) { this.autoClose = autoClose; }
    public Boolean getAllowEarlyClose() { return allowEarlyClose; }
    public void setAllowEarlyClose(Boolean allowEarlyClose) { this.allowEarlyClose = allowEarlyClose; }
    public Boolean getAllowPause() { return allowPause; }
    public void setAllowPause(Boolean allowPause) { this.allowPause = allowPause; }
    public Boolean getAllowRenewal() { return allowRenewal; }
    public void setAllowRenewal(Boolean allowRenewal) { this.allowRenewal = allowRenewal; }
    public OffsetDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public OffsetDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(OffsetDateTime effectiveTo) { this.effectiveTo = effectiveTo; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
