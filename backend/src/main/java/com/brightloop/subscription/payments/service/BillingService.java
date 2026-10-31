package com.brightloop.subscription.payments.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceLine;
import com.brightloop.subscription.billing.repository.InvoiceLineRepository;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.discounts.entity.DiscountRedemption;
import com.brightloop.subscription.discounts.repository.DiscountRedemptionRepository;
import com.brightloop.subscription.payments.dto.PaymentDto;
import com.brightloop.subscription.payments.dto.PaymentRequest;
import com.brightloop.subscription.payments.entity.Payment;
import com.brightloop.subscription.payments.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final DiscountRedemptionRepository discountRedemptionRepository;

    public BillingService(
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository,
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            DiscountRedemptionRepository discountRedemptionRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.discountRedemptionRepository = discountRedemptionRepository;
    }

    private Long getStaffOrAdminUserId() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()) || "STAFF".equalsIgnoreCase(u.getRole()))
                .map(User::getId)
                .findFirst()
                .orElseGet(() -> userRepository.findAll().stream().findFirst().map(User::getId).orElse(1L));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllInvoices(String statusFilter, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        List<Invoice> invoices;
        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            Long custId = principal.getCustomerId();
            if (custId == null) {
                return List.of();
            }
            invoices = invoiceRepository.findByCustomerId(custId);
            if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
                invoices = invoices.stream()
                        .filter(i -> statusFilter.equalsIgnoreCase(i.getStatus()))
                        .collect(Collectors.toList());
            }
        } else {
            invoices = (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter))
                    ? invoiceRepository.findByStatus(statusFilter.toUpperCase())
                    : invoiceRepository.findAllByOrderByIssueDateDesc();
        }

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
    public Map<String, Object> getInvoiceDetails(Long id, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));

        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(inv.getCustomerId())) {
                // Return 404 so other customers' invoices are not revealed
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
            }
        }

        Customer cust = customerRepository.findById(inv.getCustomerId()).orElse(null);
        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceId(inv.getId());
        List<Payment> payments = paymentRepository.findByInvoiceId(inv.getId());
        List<DiscountRedemption> discounts = discountRedemptionRepository.findByInvoiceId(inv.getId());

        List<PaymentDto> paymentDtos = payments.stream().map(this::toPaymentDto).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("id", inv.getId());
        result.put("number", inv.getNumber());
        result.put("customerId", inv.getCustomerId());
        result.put("status", inv.getStatus());
        result.put("issueDate", inv.getIssueDate());
        result.put("dueDate", inv.getDueDate());
        result.put("subtotalPaise", inv.getSubtotalPaise());
        result.put("discountPaise", inv.getDiscountPaise());
        result.put("taxPaise", inv.getTaxPaise());
        result.put("totalPaise", inv.getTotalPaise());
        result.put("amountPaidPaise", inv.getAmountPaidPaise());
        result.put("balancePaise", Math.max(0L, inv.getTotalPaise() - inv.getAmountPaidPaise()));
        result.put("customer", cust != null ? Map.of(
                "id", cust.getId(),
                "companyName", cust.getCompanyName(),
                "contactName", cust.getContactName()
        ) : null);
        result.put("lines", lines);
        result.put("payments", paymentDtos);
        result.put("discounts", discounts);
        return result;
    }

    @Transactional
    public Map<String, Object> confirmInvoice(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        inv.setStatus("CONFIRMED");
        invoiceRepository.save(inv);
        return Map.of("id", inv.getId(), "number", inv.getNumber(), "status", inv.getStatus());
    }

    @Transactional
    public Map<String, Object> cancelInvoice(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        inv.setStatus("CANCELLED");
        invoiceRepository.save(inv);
        return Map.of("id", inv.getId(), "number", inv.getNumber(), "status", inv.getStatus());
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getPayments(UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            Long custId = principal.getCustomerId();
            if (custId == null) {
                return List.of();
            }
            List<Invoice> custInvoices = invoiceRepository.findByCustomerId(custId);
            Set<Long> invoiceIds = custInvoices.stream().map(Invoice::getId).collect(Collectors.toSet());
            return paymentRepository.findAllByOrderByPaidOnDesc().stream()
                    .filter(p -> invoiceIds.contains(p.getInvoiceId()))
                    .map(this::toPaymentDto)
                    .collect(Collectors.toList());
        }
        return paymentRepository.findAllByOrderByPaidOnDesc().stream()
                .map(this::toPaymentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto recordPayment(PaymentRequest req) {
        Invoice inv = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found: " + req.getInvoiceId()));

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

        return toPaymentDto(payment);
    }

    public PaymentDto toPaymentDto(Payment p) {
        return new PaymentDto(
                p.getId(),
                p.getInvoiceId(),
                p.getMethod(),
                p.getAmountPaise(),
                p.getPaidOn(),
                p.getReference(),
                p.getNote()
        );
    }
}
