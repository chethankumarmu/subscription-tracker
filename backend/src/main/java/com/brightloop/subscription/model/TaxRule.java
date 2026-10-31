package com.brightloop.subscription.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tax_rules")
public class TaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "rate_bps", nullable = false)
    private Integer rateBps;

    @Column(name = "applies_to", nullable = false, length = 50)
    private String appliesTo = "ALL";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "effective_from", nullable = false)
    private OffsetDateTime effectiveFrom = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public TaxRule() {}

    public TaxRule(Long id, String name, Integer rateBps, String appliesTo, Boolean isActive, OffsetDateTime effectiveFrom, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.rateBps = rateBps;
        this.appliesTo = appliesTo != null ? appliesTo : "ALL";
        this.isActive = isActive != null ? isActive : true;
        this.effectiveFrom = effectiveFrom != null ? effectiveFrom : OffsetDateTime.now();
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : OffsetDateTime.now();
    }

    public static TaxRuleBuilder builder() {
        return new TaxRuleBuilder();
    }

    public static class TaxRuleBuilder {
        private Long id;
        private String name;
        private Integer rateBps;
        private String appliesTo = "ALL";
        private Boolean isActive = true;
        private OffsetDateTime effectiveFrom = OffsetDateTime.now();
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();

        public TaxRuleBuilder id(Long id) { this.id = id; return this; }
        public TaxRuleBuilder name(String name) { this.name = name; return this; }
        public TaxRuleBuilder rateBps(Integer rateBps) { this.rateBps = rateBps; return this; }
        public TaxRuleBuilder applies_to(String appliesTo) { this.appliesTo = appliesTo; return this; }
        public TaxRuleBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public TaxRuleBuilder effectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; return this; }
        public TaxRuleBuilder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TaxRuleBuilder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public TaxRule build() {
            return new TaxRule(id, name, rateBps, appliesTo, isActive, effectiveFrom, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRateBps() { return rateBps; }
    public void setRateBps(Integer rateBps) { this.rateBps = rateBps; }
    public String getAppliesTo() { return appliesTo; }
    public void setAppliesTo(String appliesTo) { this.appliesTo = appliesTo; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public OffsetDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
