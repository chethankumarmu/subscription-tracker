package com.brightloop.subscription.plans.controller;

import com.brightloop.subscription.plans.dto.PlanDto;
import com.brightloop.subscription.plans.dto.PlanVersionDto;
import com.brightloop.subscription.plans.dto.PublishedPlanDto;
import com.brightloop.subscription.plans.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    /**
     * Returns only PUBLISHED plans with current version, price, cycle, and limits.
     * Accessible by any authenticated user.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PublishedPlanDto>> getPublishedPlans() {
        List<PublishedPlanDto> plans = planService.getPublishedPlans();
        return ResponseEntity.ok(plans);
    }

    /**
     * Returns all versions for a plan.
     * Accessible by ADMIN and STAFF.
     */
    @GetMapping("/{planId}/versions")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<PlanVersionDto>> getPlanVersions(@PathVariable("planId") Long planId) {
        List<PlanVersionDto> versions = planService.getPlanVersions(planId);
        return ResponseEntity.ok(versions);
    }

    /**
     * Returns plan details by ID.
     */
    @GetMapping("/{planId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlanDto> getPlan(@PathVariable("planId") Long planId) {
        PlanDto plan = planService.getPlan(planId);
        return ResponseEntity.ok(plan);
    }
}
