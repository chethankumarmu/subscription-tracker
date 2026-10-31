package com.brightloop.subscription.reporting.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.reporting.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        if ("ADMIN".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(dashboardService.getAdminStats());
        } else if ("STAFF".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.ok(dashboardService.getStaffStats());
        } else {
            return ResponseEntity.ok(dashboardService.getCustomerStats(principal.getCustomerId()));
        }
    }
}
