package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.PaymentRequest;
import com.brightloop.subscription.model.Payment;
import com.brightloop.subscription.repository.PaymentRepository;
import com.brightloop.subscription.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final BillingService billingService;
    private final PaymentRepository paymentRepository;

    public PaymentController(BillingService billingService, PaymentRepository paymentRepository) {
        this.billingService = billingService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentRepository.findAllByOrderByPaidOnDesc());
    }

    @PostMapping
    public ResponseEntity<Payment> record(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(billingService.recordPayment(request));
    }
}
