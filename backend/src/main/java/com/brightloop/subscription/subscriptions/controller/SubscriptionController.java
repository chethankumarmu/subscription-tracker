package com.brightloop.subscription.subscriptions.controller;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Renews a subscription.
     * CUSTOMER can renew only their own subscription; ADMIN and STAFF can renew any.
     */
    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<RenewalResponseDto> renewSubscription(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        String username = authentication != null ? authentication.getName() : "";
        boolean isAdminOrStaff = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_STAFF") || role.equals("ADMIN") || role.equals("STAFF"));

        RenewalResponseDto response = subscriptionService.renewSubscription(id, username, isAdminOrStaff);
        return ResponseEntity.ok(response);
    }

    /**
     * Activates a subscription and triggers first invoice generation and PDF email.
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, Object>> activateSubscription(@PathVariable("id") Long id) {
        Invoice invoice = subscriptionService.activateSubscription(id);
        return ResponseEntity.ok(Map.of(
                "message", "Subscription activated successfully",
                "invoiceId", invoice.getId(),
                "invoiceNumber", invoice.getNumber(),
                "status", invoice.getStatus()
        ));
    }
}
