package com.brightloop.subscription.subscriptions.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.subscriptions.dto.CreateSubscriptionRequest;
import com.brightloop.subscription.subscriptions.dto.RenewalRequestResponseDto;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.dto.SubscriptionResponseDto;
import com.brightloop.subscription.subscriptions.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request,
            Authentication authentication
    ) {
        String username = authentication != null ? authentication.getName() : "";
        boolean isAdminOrStaff = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_STAFF") || role.equals("ADMIN") || role.equals("STAFF"));

        SubscriptionResponseDto response = subscriptionService.createSubscription(request, username, isAdminOrStaff);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<List<SubscriptionResponseDto>> getAllSubscriptions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions(principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<SubscriptionResponseDto> getSubscriptionById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id, principal));
    }

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

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SubscriptionResponseDto> pauseSubscription(@PathVariable("id") Long id) {
        return ResponseEntity.ok(subscriptionService.pauseSubscription(id));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponseDto> closeSubscription(@PathVariable("id") Long id) {
        return ResponseEntity.ok(subscriptionService.closeSubscription(id));
    }

    @PostMapping("/{id}/renewal-request")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<RenewalRequestResponseDto> requestRenewal(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(subscriptionService.requestRenewal(id, principal));
    }

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
}
