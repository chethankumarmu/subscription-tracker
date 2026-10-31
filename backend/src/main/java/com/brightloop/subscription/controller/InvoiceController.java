package com.brightloop.subscription.controller;

import com.brightloop.subscription.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final BillingService billingService;

    public InvoiceController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(billingService.getAllInvoices(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getInvoiceDetails(id));
    }
}
