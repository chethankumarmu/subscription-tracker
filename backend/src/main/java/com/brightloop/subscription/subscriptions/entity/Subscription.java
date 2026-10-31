package com.brightloop.subscription.subscriptions.entity;

import com.brightloop.subscription.plans.entity.PlanVersion;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "plan_version_id", nullable = false)
    private Long planVersionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_version_id", insertable = false, updatable = false)
    private PlanVersion planVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.DRAFT;

    @Column(name = "is_paused", nullable = false)
    @Builder.Default
    private boolean paused = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "payment_terms_days", nullable = false)
    @Builder.Default
    private Integer paymentTermsDays = 30;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "quotation_valid_until")
    private LocalDate quotationValidUntil;

    @Column(name = "close_reason")
    private String closeReason;

    @Column(name = "created_by")
    private Long createdBy;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}

