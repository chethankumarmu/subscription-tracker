package com.brightloop.subscription.payments.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.payments.dto.PaymentDto;
import com.brightloop.subscription.payments.dto.PaymentRequest;
import com.brightloop.subscription.payments.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final BillingService billingService;

    public PaymentController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<List<PaymentDto>> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(billingService.getPayments(principal));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PaymentDto> record(@RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.recordPayment(request));
    }
}
