package com.brightloop.subscription.service;

import com.brightloop.subscription.dto.PaymentRequest;
import com.brightloop.subscription.model.*;
import com.brightloop.subscription.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public BillingService(
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository,
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    private Long getStaffOrAdminUserId() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()) || "STAFF".equalsIgnoreCase(u.getRole()))
                .map(User::getId)
                .findFirst()
                .orElseGet(() -> userRepository.findAll().stream().findFirst().map(User::getId).orElse(1L));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllInvoices(String statusFilter) {
        List<Invoice> invoices = (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter))
                ? invoiceRepository.findByStatus(statusFilter.toUpperCase())
                : invoiceRepository.findAllByOrderByIssueDateDesc();

        Map<Long, Customer> customerMap = new HashMap<>();
        customerRepository.findAll().forEach(c -> customerMap.put(c.getId(), c));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Invoice inv : invoices) {
            Customer cust = customerMap.get(inv.getCustomerId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", inv.getId());
            map.put("number", inv.getNumber());
            map.put("customerId", inv.getCustomerId());
            map.put("customerName", cust != null ? cust.getCompanyName() : "Acme Corp");
            map.put("contactName", cust != null ? cust.getContactName() : "John Doe");
            map.put("status", inv.getStatus());
            map.put("issueDate", inv.getIssueDate());
            map.put("dueDate", inv.getDueDate());
            map.put("currency", inv.getCurrency());
            map.put("subtotalPaise", inv.getSubtotalPaise());
            map.put("discountPaise", inv.getDiscountPaise());
            map.put("taxPaise", inv.getTaxPaise());
            map.put("totalPaise", inv.getTotalPaise());
            map.put("amountPaidPaise", inv.getAmountPaidPaise());
            map.put("balancePaise", Math.max(0L, inv.getTotalPaise() - inv.getAmountPaidPaise()));
            result.add(map);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInvoiceDetails(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));

        Customer cust = customerRepository.findById(inv.getCustomerId()).orElse(null);
        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceId(inv.getId());
        List<Payment> payments = paymentRepository.findByInvoiceId(inv.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("invoice", inv);
        result.put("customer", cust);
        result.put("lines", lines);
        result.put("payments", payments);
        result.put("balancePaise", Math.max(0L, inv.getTotalPaise() - inv.getAmountPaidPaise()));
        return result;
    }

    @Transactional
    public Payment recordPayment(PaymentRequest req) {
        Invoice inv = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + req.getInvoiceId()));

        Payment payment = paymentRepository.save(Payment.builder()
                .invoiceId(inv.getId())
                .method(req.getMethod() != null ? req.getMethod().toUpperCase() : "CARD")
                .amountPaise(req.getAmountPaise())
                .paidOn(LocalDate.now())
                .reference(req.getReference() != null && !req.getReference().isBlank()
                        ? req.getReference()
                        : "TXN_" + System.currentTimeMillis() % 1000000)
                .note(req.getNote())
                .recordedBy(getStaffOrAdminUserId())
                .build());

        long newAmountPaid = inv.getAmountPaidPaise() + req.getAmountPaise();
        inv.setAmountPaidPaise(newAmountPaid);
        if (newAmountPaid >= inv.getTotalPaise()) {
            inv.setStatus("PAID");
        }
        invoiceRepository.save(inv);

        return payment;
    }
}
