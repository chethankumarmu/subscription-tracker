package com.brightloop.subscription.notifications.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceLine;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicePdfServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Test
    void testGenerateInvoicePdf_ProducesValidPdf() {
        InvoicePdfService pdfService = new InvoicePdfService(invoiceRepository);

        Customer customer = Customer.builder()
                .contactName("Test Customer")
                .companyName("Acme Corp")
                .billingAddress("123 Tech Park, Bengaluru, KA 560001")
                .build();

        Subscription subscription = Subscription.builder()
                .number("SUB-2026-000123")
                .build();

        Invoice invoice = Invoice.builder()
                .id(10L)
                .number("INV-2026-000045")
                .customer(customer)
                .subscription(subscription)
                .status("CONFIRMED")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .subtotalPaise(149900L)
                .discountPaise(0L)
                .taxPaise(26982L)
                .totalPaise(176882L)
                .build();

        InvoiceLine line = InvoiceLine.builder()
                .description("Odoo Business Suite")
                .quantity(1)
                .unitPricePaise(149900L)
                .taxPaise(26982L)
                .lineTotalPaise(176882L)
                .build();

        invoice.setLines(List.of(line));

        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));

        byte[] pdfBytes = pdfService.generateInvoicePdf(10L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF size should be substantial");
        // Verify PDF magic header bytes: %PDF-
        String header = new String(pdfBytes, 0, Math.min(pdfBytes.length, 5));
        assertEquals("%PDF-", header);
    }
}
