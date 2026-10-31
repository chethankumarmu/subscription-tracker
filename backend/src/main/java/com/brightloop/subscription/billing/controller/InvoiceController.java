package com.brightloop.subscription.billing.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.notifications.service.InvoicePdfService;
import com.brightloop.subscription.payments.service.BillingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final BillingService billingService;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(BillingService billingService, InvoicePdfService invoicePdfService) {
        this.billingService = billingService;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<List<Map<String, Object>>> getAll(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(billingService.getAllInvoices(status, principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(billingService.getInvoiceDetails(id, principal));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, Object>> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.confirmInvoice(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.cancelInvoice(id));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        // Enforce ownership check: throws 404 if customer does not own this invoice
        billingService.getInvoiceDetails(id, principal);

        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + id + ".pdf\"")
                .body(pdfBytes);
    }
}
