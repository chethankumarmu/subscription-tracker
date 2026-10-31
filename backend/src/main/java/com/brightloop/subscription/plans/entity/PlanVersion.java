package com.brightloop.subscription.plans.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "plan_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle;

    @Column(name = "min_quantity", nullable = false)
    @Builder.Default
    private Integer minQuantity = 1;

    @Column(name = "available_from", nullable = false)
    private LocalDate availableFrom;

    @Column(name = "available_to")
    private LocalDate availableTo;

    @Column(name = "auto_close", nullable = false)
    @Builder.Default
    private boolean autoClose = false;

    @Column(name = "allow_early_close", nullable = false)
    @Builder.Default
    private boolean allowEarlyClose = false;

    @Column(name = "allow_pause", nullable = false)
    @Builder.Default
    private boolean allowPause = false;

    @Column(name = "allow_renewal", nullable = false)
    @Builder.Default
    private boolean allowRenewal = true;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private Instant effectiveFrom = Instant.now();

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}

