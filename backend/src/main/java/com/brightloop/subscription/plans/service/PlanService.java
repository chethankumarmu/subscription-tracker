package com.brightloop.subscription.plans.service;

import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.repository.ProductRepository;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.brightloop.subscription.plans.dto.*;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanLimit;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanLimitRepository;
import com.brightloop.subscription.plans.repository.PlanRepository;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanVersionRepository planVersionRepository;
    private final PlanLimitRepository planLimitRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public PlanService(
            PlanRepository planRepository,
            PlanVersionRepository planVersionRepository,
            PlanLimitRepository planLimitRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.planRepository = planRepository;
        this.planVersionRepository = planVersionRepository;
        this.planLimitRepository = planLimitRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new plan in DRAFT status.
     */
    @Transactional
    public PlanDto createPlan(CreatePlanRequest request) {
        if (planRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Plan with code " + request.getCode() + " already exists");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId()));

        Plan plan = Plan.builder()
                .code(request.getCode())
                .name(request.getName())
                .product(product)
                .status("DRAFT")
                .currentVersion(null)
                .build();

        Plan saved = planRepository.save(plan);
        return toPlanDto(saved);
    }

    /**
     * Adds a new version to an existing plan with version_no = max + 1.
     * The new version starts in DRAFT status.
     */
    @Transactional
    public PlanVersionDto addVersion(Long planId, CreatePlanVersionRequest request, String username) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));

        if (request.getAvailableTo() != null && request.getAvailableTo().isBefore(request.getAvailableFrom())) {
            throw new IllegalArgumentException("availableTo cannot be before availableFrom");
        }

        User creator = resolveUser(username);

        int nextVersionNo = planVersionRepository.findTopByPlanIdOrderByVersionNoDesc(planId)
                .map(v -> v.getVersionNo() + 1)
                .orElse(1);

        PlanVersion version = PlanVersion.builder()
                .plan(plan)
                .versionNo(nextVersionNo)
                .status("DRAFT")
                .pricePaise(request.getPricePaise())
                .billingCycle(request.getBillingCycle().toUpperCase())
                .minQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 1)
                .availableFrom(request.getAvailableFrom())
                .availableTo(request.getAvailableTo())
                .autoClose(Boolean.TRUE.equals(request.getAutoClose()))
                .allowEarlyClose(Boolean.TRUE.equals(request.getAllowEarlyClose()))
                .allowPause(Boolean.TRUE.equals(request.getAllowPause()))
                .allowRenewal(request.getAllowRenewal() == null || Boolean.TRUE.equals(request.getAllowRenewal()))
                .effectiveFrom(OffsetDateTime.now())
                .effectiveTo(null)
                .createdBy(creator)
                .build();

        PlanVersion savedVersion = planVersionRepository.save(version);

        if (request.getLimits() != null && !request.getLimits().isEmpty()) {
            List<PlanLimit> limitEntities = new ArrayList<>();
            for (PlanLimitDto lDto : request.getLimits()) {
                validateLimitKey(lDto.getLimitKey());
                PlanLimit limit = PlanLimit.builder()
                        .planVersion(savedVersion)
                        .limitKey(lDto.getLimitKey().toUpperCase())
                        .limitValue(lDto.getLimitValue())
                        .build();
                limitEntities.add(planLimitRepository.save(limit));
            }
            savedVersion.setLimits(limitEntities);
        }

        return toPlanVersionDto(savedVersion);
    }

    /**
     * Rejects edits to any published version.
     */
    public void validateCanEditVersion(Long versionId) {
        PlanVersion version = planVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found with id: " + versionId));

        if ("PUBLISHED".equals(version.getStatus()) || version.getEffectiveTo() != null) {
            throw new IllegalStateException("Editing an existing published version is forbidden; create a new version instead");
        }
    }

    /**
     * Publishes a plan version.
     * Validates price > 0, availability window valid, at least one limit defined.
     * Sets effective_from = now, closes previous published version (effective_to = now),
     * sets plan.status = PUBLISHED and plan.current_version_id = this version.
     * Runs in a single transaction.
     */
    @Transactional
    public PlanVersionDto publishVersion(Long versionId) {
        PlanVersion version = planVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found with id: " + versionId));

        // 1. Validate price > 0
        if (version.getPricePaise() == null || version.getPricePaise() <= 0) {
            throw new IllegalStateException("Price must be greater than 0 to publish a version");
        }

        // 2. Validate availability window
        if (version.getAvailableTo() != null && version.getAvailableTo().isBefore(version.getAvailableFrom())) {
            throw new IllegalStateException("Availability window is invalid: availableTo is before availableFrom");
        }

        // 3. Validate at least one limit defined
        List<PlanLimit> limits = planLimitRepository.findByPlanVersionId(versionId);
        if (limits == null || limits.isEmpty()) {
            throw new IllegalStateException("At least one limit must be defined to publish a plan version");
        }

        OffsetDateTime now = OffsetDateTime.now();

        // 4. Close previous published version
        Optional<PlanVersion> currentPublishedOpt = planVersionRepository.findCurrentPublishedVersion(version.getPlan().getId());
        if (currentPublishedOpt.isPresent() && !currentPublishedOpt.get().getId().equals(version.getId())) {
            PlanVersion prev = currentPublishedOpt.get();
            prev.setEffectiveTo(now);
            prev.setStatus("RETIRED");
            planVersionRepository.save(prev);
        }

        // 5. Activate this version
        version.setEffectiveFrom(now);
        version.setEffectiveTo(null);
        version.setStatus("PUBLISHED");
        PlanVersion savedVersion = planVersionRepository.save(version);

        // 6. Update plan
        Plan plan = version.getPlan();
        plan.setStatus("PUBLISHED");
        plan.setCurrentVersion(savedVersion);
        planRepository.save(plan);

        savedVersion.setLimits(limits);
        return toPlanVersionDto(savedVersion);
    }

    /**
     * Retires a plan version. Sets effective_to = now, and plan.status = RETIRED if no published version remains.
     */
    @Transactional
    public PlanVersionDto retireVersion(Long versionId) {
        PlanVersion version = planVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found with id: " + versionId));

        OffsetDateTime now = OffsetDateTime.now();
        version.setEffectiveTo(now);
        version.setStatus("RETIRED");
        PlanVersion savedVersion = planVersionRepository.save(version);

        // Check if any published version remains
        Optional<PlanVersion> remaining = planVersionRepository.findCurrentPublishedVersion(version.getPlan().getId());
        Plan plan = version.getPlan();
        if (remaining.isEmpty()) {
            plan.setStatus("RETIRED");
            planRepository.save(plan);
        }

        List<PlanLimit> limits = planLimitRepository.findByPlanVersionId(versionId);
        savedVersion.setLimits(limits);
        return toPlanVersionDto(savedVersion);
    }

    /**
     * Returns only PUBLISHED plans with current version, price, cycle, and limits.
     */
    @Transactional(readOnly = true)
    public List<PublishedPlanDto> getPublishedPlans() {
        return planRepository.findByStatus("PUBLISHED").stream()
                .filter(p -> p.getCurrentVersion() != null
                        && "PUBLISHED".equals(p.getCurrentVersion().getStatus())
                        && p.getCurrentVersion().getEffectiveTo() == null)
                .map(p -> {
                    PlanVersion v = p.getCurrentVersion();
                    List<PlanLimit> limits = planLimitRepository.findByPlanVersionId(v.getId());
                    Map<String, Long> limitMap = limits.stream()
                            .collect(Collectors.toMap(PlanLimit::getLimitKey, l -> l.getLimitValue() != null ? l.getLimitValue() : -1L, (a, b) -> a));

                    // Replace -1 with null for unlimited representation in Map if needed
                    Map<String, Long> cleanLimits = new LinkedHashMap<>();
                    for (PlanLimit l : limits) {
                        cleanLimits.put(l.getLimitKey(), l.getLimitValue());
                    }

                    return new PublishedPlanDto(
                            p.getId(),
                            p.getCode(),
                            p.getName(),
                            p.getStatus(),
                            v.getId(),
                            v.getVersionNo(),
                            v.getPricePaise(),
                            MoneyUtils.formatPaiseToRupees(v.getPricePaise()),
                            v.getBillingCycle(),
                            v.getMinQuantity(),
                            cleanLimits
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns all versions for a plan.
     */
    @Transactional(readOnly = true)
    public List<PlanVersionDto> getPlanVersions(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new IllegalArgumentException("Plan not found with id: " + planId);
        }
        return planVersionRepository.findByPlanIdOrderByVersionNoAsc(planId).stream()
                .map(this::toPlanVersionDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a plan by id.
     */
    @Transactional(readOnly = true)
    public PlanDto getPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));
        return toPlanDto(plan);
    }

    public PlanDto toPlanDto(Plan plan) {
        PlanVersionDto versionDto = null;
        if (plan.getCurrentVersion() != null) {
            versionDto = toPlanVersionDto(plan.getCurrentVersion());
        }

        return new PlanDto(
                plan.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getProduct() != null ? plan.getProduct().getId() : null,
                plan.getProduct() != null ? plan.getProduct().getName() : null,
                plan.getStatus(),
                versionDto,
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }

    public PlanVersionDto toPlanVersionDto(PlanVersion version) {
        List<PlanLimit> limits = version.getLimits();
        if (limits == null || limits.isEmpty()) {
            limits = planLimitRepository.findByPlanVersionId(version.getId());
        }

        Map<String, Long> limitMap = new LinkedHashMap<>();
        if (limits != null) {
            for (PlanLimit limit : limits) {
                limitMap.put(limit.getLimitKey(), limit.getLimitValue());
            }
        }

        return new PlanVersionDto(
                version.getId(),
                version.getPlan() != null ? version.getPlan().getId() : null,
                version.getPlan() != null ? version.getPlan().getCode() : null,
                version.getPlan() != null ? version.getPlan().getName() : null,
                version.getVersionNo(),
                version.getStatus(),
                version.getPricePaise(),
                version.getPricePaise() != null ? MoneyUtils.formatPaiseToRupees(version.getPricePaise()) : null,
                version.getBillingCycle(),
                version.getMinQuantity(),
                version.getAvailableFrom(),
                version.getAvailableTo(),
                version.getAutoClose(),
                version.getAllowEarlyClose(),
                version.getAllowPause(),
                version.getAllowRenewal(),
                version.getEffectiveFrom(),
                version.getEffectiveTo(),
                limitMap,
                version.getCreatedAt()
        );
    }

    private User resolveUser(String username) {
        if (username != null && !username.isBlank()) {
            Optional<User> u = userRepository.findByEmail(username);
            if (u.isPresent()) {
                return u.get();
            }
        }
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No admin user found to record version creation"));
    }

    private void validateLimitKey(String key) {
        if (key == null || (!key.equalsIgnoreCase("USERS")
                && !key.equalsIgnoreCase("STORAGE_GB")
                && !key.equalsIgnoreCase("API_CALLS")
                && !key.equalsIgnoreCase("CUSTOM_DOMAINS"))) {
            throw new IllegalArgumentException("limitKey must be one of: USERS, STORAGE_GB, API_CALLS, CUSTOM_DOMAINS");
        }
    }
}
