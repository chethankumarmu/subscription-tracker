package com.brightloop.subscription.jobs.service;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BillingSubscriptionProcessor {

    public enum ProcessResult {
        CREATED,
        SKIPPED,
        FAILED
    }

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final EmailOutboxService emailOutboxService;

    public BillingSubscriptionProcessor(
            SubscriptionRepository subscriptionRepository,
            InvoiceRepository invoiceRepository,
            InvoiceService invoiceService,
            EmailOutboxService emailOutboxService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
        this.emailOutboxService = emailOutboxService;
    }

    /**
     * Executes billing logic for a single subscription in its own isolated transaction.
     * One failed subscription rolls back only its own transaction and does not abort the run.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessResult processSubscriptionInTransaction(Subscription sub, LocalDate targetDate) {
        if (sub == null || !"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
            return ProcessResult.SKIPPED;
        }

        LocalDate nextBillingDate = sub.getNextBillingDate();
        if (nextBillingDate == null || nextBillingDate.isAfter(targetDate)) {
            return ProcessResult.SKIPPED;
        }

        String cycle = sub.getPlanVersion() != null ? sub.getPlanVersion().getBillingCycle() : "MONTHLY";
        LocalDate periodStart = nextBillingDate;
        LocalDate periodEnd = invoiceService.calculatePeriodEnd(periodStart, cycle);

        // Check idempotency: rely on unique constraint (subscription_id, period_start)
        if (invoiceRepository != null) {
            Optional<Invoice> existing = invoiceRepository.findBySubscriptionIdAndPeriodStart(sub.getId(), periodStart);
            if (existing.isPresent()) {
                if (!sub.getNextBillingDate().isAfter(periodEnd)) {
                    sub.setNextBillingDate(periodEnd.plusDays(1));
                    subscriptionRepository.save(sub);
                }
                return ProcessResult.SKIPPED;
            }
        }

        Invoice invoice = invoiceService.generateInvoiceForSubscription(sub, periodStart, periodEnd, "CONFIRMED");
        sub.setNextBillingDate(periodEnd.plusDays(1));
        subscriptionRepository.save(sub);

        if (emailOutboxService != null) {
            emailOutboxService.queueInvoiceEmail(invoice);
        }

        return ProcessResult.CREATED;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessResult processSubscriptionInTransaction(Long subscriptionId, LocalDate targetDate) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));
        return processSubscriptionInTransaction(sub, targetDate);
    }
}
