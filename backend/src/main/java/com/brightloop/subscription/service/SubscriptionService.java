package com.brightloop.subscription.service;

import com.brightloop.subscription.dto.SubscriptionRequest;
import com.brightloop.subscription.model.*;
import com.brightloop.subscription.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionLineRepository subscriptionLineRepository;
    private final PlanRepository planRepository;
    private final PlanVersionRepository planVersionRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionLineRepository subscriptionLineRepository,
            PlanRepository planRepository,
            PlanVersionRepository planVersionRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            TaxRuleRepository taxRuleRepository,
            InvoiceRepository invoiceRepository,
            InvoiceLineRepository invoiceLineRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionLineRepository = subscriptionLineRepository;
        this.planRepository = planRepository;
        this.planVersionRepository = planVersionRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.taxRuleRepository = taxRuleRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, String> customerNames = new HashMap<>();
        customerRepository.findAll().forEach(c -> customerNames.put(c.getId(), c.getCompanyName()));

        Map<Long, String> planNames = new HashMap<>();
        Map<Long, Long> planPrices = new HashMap<>();
        Map<Long, Boolean> allowPauseMap = new HashMap<>();

        for (PlanVersion pv : planVersionRepository.findAll()) {
            Plan p = planRepository.findById(pv.getPlanId()).orElse(null);
            if (p != null) {
                planNames.put(pv.getId(), p.getName());
                planPrices.put(pv.getId(), pv.getPricePaise());
                allowPauseMap.put(pv.getId(), pv.getAllowPause());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Subscription s : subs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("number", s.getNumber());
            map.put("customerId", s.getCustomerId());
            map.put("customerName", customerNames.getOrDefault(s.getCustomerId(), "Acme Corp"));
            map.put("planVersionId", s.getPlanVersionId());
            map.put("planName", planNames.getOrDefault(s.getPlanVersionId(), "Pro Tier"));
            map.put("pricePaise", planPrices.getOrDefault(s.getPlanVersionId(), 199900L));
            map.put("status", s.getStatus());
            map.put("isPaused", s.getIsPaused());
            map.put("allowPause", allowPauseMap.getOrDefault(s.getPlanVersionId(), true));
            map.put("startDate", s.getStartDate());
            map.put("nextBillingDate", s.getNextBillingDate());
            result.add(map);
        }
        return result;
    }

    @Transactional
    public Subscription createSubscription(SubscriptionRequest request) {
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + request.getPlanId()));

        PlanVersion version = planVersionRepository.findById(plan.getCurrentVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Plan version not found for plan: " + plan.getId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseGet(() -> customerRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("No customers available")));

        String subNumber = "SUB-2026-" + String.format("%04d", subscriptionRepository.count() + 1);

        Subscription sub = subscriptionRepository.save(Subscription.builder()
                .number(subNumber)
                .customerId(customer.getId())
                .planVersionId(version.getId())
                .status("ACTIVE")
                .isPaused(false)
                .startDate(LocalDate.now())
                .nextBillingDate("YEARLY".equalsIgnoreCase(request.getBillingCycle())
                        ? LocalDate.now().plusYears(1)
                        : LocalDate.now().plusMonths(1))
                .paymentTermsDays(30)
                .createdBy(1L)
                .build());

        // Line
        Product prod = productRepository.findById(plan.getProductId()).orElse(null);
        Long unitPrice = version.getPricePaise();
        if ("YEARLY".equalsIgnoreCase(request.getBillingCycle()) && unitPrice > 0) {
            unitPrice = unitPrice * 10; // 2 months free discount
        }

        TaxRule taxRule = taxRuleRepository.findByIsActiveTrue().stream().findFirst().orElse(null);

        subscriptionLineRepository.save(SubscriptionLine.builder()
                .subscriptionId(sub.getId())
                .productId(prod != null ? prod.getId() : 1L)
                .quantity(1)
                .unitPricePaise(unitPrice)
                .taxRuleId(taxRule != null ? taxRule.getId() : null)
                .lineTotalPaise(unitPrice)
                .build());

        // Generate immediate invoice for subscription
        long taxAmount = Math.round(unitPrice * 0.18);
        long totalAmount = unitPrice + taxAmount;

        String invNumber = "INV_0000" + (70 + invoiceRepository.count() + 1);
        Invoice inv = invoiceRepository.save(Invoice.builder()
                .number(invNumber)
                .subscriptionId(sub.getId())
                .customerId(customer.getId())
                .status(unitPrice == 0 ? "PAID" : "CONFIRMED")
                .periodStart(LocalDate.now())
                .periodEnd("YEARLY".equalsIgnoreCase(request.getBillingCycle())
                        ? LocalDate.now().plusYears(1)
                        : LocalDate.now().plusMonths(1))
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .currency("INR")
                .subtotalPaise(unitPrice)
                .discountPaise(0L)
                .taxPaise(taxAmount)
                .totalPaise(totalAmount)
                .amountPaidPaise(unitPrice == 0 ? totalAmount : 0L)
                .build());

        invoiceLineRepository.save(InvoiceLine.builder()
                .invoiceId(inv.getId())
                .productId(prod != null ? prod.getId() : 1L)
                .description(plan.getName() + " Subscription (" + request.getBillingCycle() + ")")
                .quantity(1)
                .unitPricePaise(unitPrice)
                .taxRuleId(taxRule != null ? taxRule.getId() : null)
                .taxRateBps(1800)
                .taxPaise(taxAmount)
                .lineTotalPaise(totalAmount)
                .build());

        return sub;
    }

    @Transactional
    public Subscription togglePause(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));

        PlanVersion version = planVersionRepository.findById(sub.getPlanVersionId()).orElse(null);
        if (version != null && !Boolean.TRUE.equals(version.getAllowPause()) && !Boolean.TRUE.equals(sub.getIsPaused())) {
            throw new IllegalStateException("Plan does not permit pausing subscription.");
        }

        sub.setIsPaused(!Boolean.TRUE.equals(sub.getIsPaused()));
        return subscriptionRepository.save(sub);
    }

    @Transactional
    public Subscription closeSubscription(Long id, String reason) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + id));
        sub.setStatus("CLOSED");
        sub.setCloseReason(reason != null ? reason : "Customer requested cancellation");
        return subscriptionRepository.save(sub);
    }
}
