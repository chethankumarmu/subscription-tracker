package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.PlanDto;
import com.brightloop.subscription.model.Plan;
import com.brightloop.subscription.model.PlanLimit;
import com.brightloop.subscription.model.PlanVersion;
import com.brightloop.subscription.repository.PlanLimitRepository;
import com.brightloop.subscription.repository.PlanRepository;
import com.brightloop.subscription.repository.PlanVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanRepository planRepository;
    private final PlanVersionRepository planVersionRepository;
    private final PlanLimitRepository planLimitRepository;

    public PlanController(
            PlanRepository planRepository,
            PlanVersionRepository planVersionRepository,
            PlanLimitRepository planLimitRepository
    ) {
        this.planRepository = planRepository;
        this.planVersionRepository = planVersionRepository;
        this.planLimitRepository = planLimitRepository;
    }

    @GetMapping
    public ResponseEntity<List<PlanDto>> getActivePlans() {
        List<Plan> plans = planRepository.findAll();
        List<PlanDto> result = new ArrayList<>();

        for (Plan plan : plans) {
            PlanVersion version = null;
            if (plan.getCurrentVersionId() != null) {
                version = planVersionRepository.findById(plan.getCurrentVersionId()).orElse(null);
            }
            if (version == null) {
                List<PlanVersion> versions = planVersionRepository.findByPlanId(plan.getId());
                if (!versions.isEmpty()) {
                    version = versions.get(0);
                }
            }

            List<PlanLimit> limits = version != null
                    ? planLimitRepository.findByPlanVersionId(version.getId())
                    : Collections.emptyList();

            Map<String, Object> featureMap = new HashMap<>();
            for (PlanLimit limit : limits) {
                featureMap.put(limit.getLimitKey(), limit.getLimitValue());
            }

            Long monthlyPaise = version != null ? version.getPricePaise() : 0L;
            Long yearlyPaise = monthlyPaise > 0 ? (monthlyPaise * 10) : 0L; // 2 months free

            result.add(PlanDto.builder()
                    .id(plan.getId())
                    .code(plan.getCode())
                    .name(plan.getName())
                    .status(plan.getStatus())
                    .currentVersionId(version != null ? version.getId() : null)
                    .monthlyPricePaise(monthlyPaise)
                    .yearlyPricePaise(yearlyPaise)
                    .billingCycle(version != null ? version.getBillingCycle() : "MONTHLY")
                    .allowPause(version != null && Boolean.TRUE.equals(version.getAllowPause()))
                    .limits(limits)
                    .featureMap(featureMap)
                    .build());
        }

        // Sort: Starter, Pro, Enterprise
        result.sort(Comparator.comparing(PlanDto::getMonthlyPricePaise));
        return ResponseEntity.ok(result);
    }
}
