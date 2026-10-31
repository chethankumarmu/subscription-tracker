package com.brightloop.subscription.plans.controller;

import com.brightloop.subscription.plans.dto.*;
import com.brightloop.subscription.plans.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminPlanController {

    private final PlanService planService;

    public AdminPlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDto> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        PlanDto plan = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @PostMapping("/plans/{planId}/versions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanVersionDto> addVersion(
            @PathVariable("planId") Long planId,
            @Valid @RequestBody CreatePlanVersionRequest request,
            Authentication authentication
    ) {
        String username = authentication != null ? authentication.getName() : null;
        PlanVersionDto version = planService.addVersion(planId, request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }

    @PostMapping("/plan-versions/{versionId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanVersionDto> publishVersion(@PathVariable("versionId") Long versionId) {
        PlanVersionDto version = planService.publishVersion(versionId);
        return ResponseEntity.ok(version);
    }

    @PostMapping("/plan-versions/{versionId}/retire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanVersionDto> retireVersion(@PathVariable("versionId") Long versionId) {
        PlanVersionDto version = planService.retireVersion(versionId);
        return ResponseEntity.ok(version);
    }
}
