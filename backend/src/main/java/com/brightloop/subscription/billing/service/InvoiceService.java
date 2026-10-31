package com.brightloop.subscription.billing.service;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceLine;
import com.brightloop.subscription.billing.entity.TaxRule;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.billing.repository.TaxRuleRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TaxRuleRepository taxRuleRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, TaxRuleRepository taxRuleRepository) {
        this.invoiceRepository = invoiceRepository;
        this.taxRuleRepository = taxRuleRepository;
    }

    /**
     * Generates an invoice for a subscription and period.
     * Idempotent: if an invoice already exists for (subscriptionId, periodStart), returns the existing invoice.
     */
    @Transactional
    public Invoice generateInvoiceForSubscription(
            Subscription subscription,
            LocalDate periodStart,
            LocalDate periodEnd,
            String initialStatus
    ) {
        // Idempotency check: rely on (subscription_id, period_start)
        Optional<Invoice> existing = invoiceRepository.findBySubscriptionIdAndPeriodStart(
                subscription.getId(), periodStart
        );
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDate issueDate = LocalDate.now();
        int termsDays = subscription.getPaymentTermsDays() != null ? subscription.getPaymentTermsDays() : 30;
        LocalDate dueDate = issueDate.plusDays(termsDays);

        String invoiceNumber = generateInvoiceNumber(periodStart);

        Invoice invoice = Invoice.builder()
                .number(invoiceNumber)
                .subscription(subscription)
                .customer(subscription.getCustomer())
                .status(initialStatus != null ? initialStatus : "CONFIRMED")
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .currency("INR")
                .subtotalPaise(0L)
                .discountPaise(0L)
                .taxPaise(0L)
                .totalPaise(0L)
                .amountPaidPaise(0L)
                .lines(new ArrayList<>())
                .build();

        long subtotal = 0L;
        long taxTotal = 0L;

        if (subscription.getLines() != null && !subscription.getLines().isEmpty()) {
            for (SubscriptionLine subLine : subscription.getLines()) {
                long unitPrice = subLine.getUnitPricePaise() != null ? subLine.getUnitPricePaise() : 0L;
                int qty = subLine.getQuantity() != null ? subLine.getQuantity() : 1;
                long lineBase = unitPrice * qty;

                int taxRateBps = 0;
                if (subLine.getTaxRuleId() != null) {
                    Optional<TaxRule> tr = taxRuleRepository.findById(subLine.getTaxRuleId());
                    if (tr.isPresent() && tr.get().getRateBps() != null) {
                        taxRateBps = tr.get().getRateBps();
                    }
                }

                long lineTax = (lineBase * taxRateBps) / 10000L;
                long lineTotal = lineBase + lineTax;

                String desc = subLine.getProduct() != null ? subLine.getProduct().getName() : "Subscription Item";
                if (subLine.getVariant() != null) {
                    desc += " (" + subLine.getVariant().getName() + ")";
                }

                InvoiceLine invLine = InvoiceLine.builder()
                        .invoice(invoice)
                        .product(subLine.getProduct())
                        .variant(subLine.getVariant())
                        .description(desc)
                        .quantity(qty)
                        .unitPricePaise(unitPrice)
                        .discountPaise(0L)
                        .taxRuleId(subLine.getTaxRuleId())
                        .taxRateBps(taxRateBps)
                        .taxPaise(lineTax)
                        .lineTotalPaise(lineTotal)
                        .build();

                invoice.getLines().add(invLine);
                subtotal += lineBase;
                taxTotal += lineTax;
            }
        } else {
            // Default plan line
            long planPrice = (subscription.getPlanVersion() != null && subscription.getPlanVersion().getPricePaise() != null)
                    ? subscription.getPlanVersion().getPricePaise()
                    : 0L;

            String planName = (subscription.getPlanVersion() != null && subscription.getPlanVersion().getPlan() != null)
                    ? subscription.getPlanVersion().getPlan().getName()
                    : "Subscription Service";

            InvoiceLine invLine = InvoiceLine.builder()
                    .invoice(invoice)
                    .product(subscription.getPlanVersion() != null && subscription.getPlanVersion().getPlan() != null
                            ? subscription.getPlanVersion().getPlan().getProduct()
                            : null)
                    .description(planName + " (" + subscription.getNumber() + ")")
                    .quantity(1)
                    .unitPricePaise(planPrice)
                    .discountPaise(0L)
                    .taxRuleId(null)
                    .taxRateBps(0)
                    .taxPaise(0L)
                    .lineTotalPaise(planPrice)
                    .build();

            invoice.getLines().add(invLine);
            subtotal = planPrice;
            taxTotal = 0L;
        }

        invoice.setSubtotalPaise(subtotal);
        invoice.setDiscountPaise(0L);
        invoice.setTaxPaise(taxTotal);
        invoice.setTotalPaise(subtotal + taxTotal);

        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> findLatestInvoiceForSubscription(Long subscriptionId) {
        return invoiceRepository.findFirstBySubscriptionIdOrderByPeriodStartDesc(subscriptionId);
    }

    public LocalDate calculatePeriodEnd(LocalDate periodStart, String billingCycle) {
        if (billingCycle == null) {
            return periodStart.plusMonths(1).minusDays(1);
        }
        return switch (billingCycle.toUpperCase()) {
            case "DAILY" -> periodStart;
            case "WEEKLY" -> periodStart.plusWeeks(1).minusDays(1);
            case "YEARLY" -> periodStart.plusYears(1).minusDays(1);
            case "MONTHLY" -> periodStart.plusMonths(1).minusDays(1);
            default -> periodStart.plusMonths(1).minusDays(1);
        };
    }

    public LocalDate extendExpiryDate(LocalDate currentExpiry, String billingCycle) {
        if (currentExpiry == null) {
            currentExpiry = LocalDate.now();
        }
        if (billingCycle == null) {
            return currentExpiry.plusMonths(1);
        }
        return switch (billingCycle.toUpperCase()) {
            case "DAILY" -> currentExpiry.plusDays(1);
            case "WEEKLY" -> currentExpiry.plusWeeks(1);
            case "YEARLY" -> currentExpiry.plusYears(1);
            case "MONTHLY" -> currentExpiry.plusMonths(1);
            default -> currentExpiry.plusMonths(1);
        };
    }

    private String generateInvoiceNumber(LocalDate date) {
        long count = invoiceRepository.count() + 1;
        String candidate = "INV-" + date.getYear() + "-" + String.format("%06d", count);
        if (invoiceRepository.findByNumber(candidate).isPresent()) {
            candidate = "INV-" + date.getYear() + "-" + String.format("%06d", count + (System.currentTimeMillis() % 1000));
        }
        return candidate;
    }
}
