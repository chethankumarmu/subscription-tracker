package com.brightloop.subscription.subscriptions.service;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.common.util.MoneyUtils;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceService invoiceService;
    private final EmailOutboxService emailOutboxService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService,
            EmailOutboxService emailOutboxService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceService = invoiceService;
        this.emailOutboxService = emailOutboxService;
    }

    /**
     * Activates a subscription, generates its first invoice if none exists,
     * and queues the invoice PDF email.
     */
    @Transactional
    public Invoice activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus("ACTIVE");

        LocalDate startDate = subscription.getStartDate() != null ? subscription.getStartDate() : LocalDate.now();
        subscription.setStartDate(startDate);

        String cycle = subscription.getPlanVersion() != null ? subscription.getPlanVersion().getBillingCycle() : "MONTHLY";
        LocalDate periodEnd = invoiceService.calculatePeriodEnd(startDate, cycle);

        if (subscription.getExpiryDate() == null) {
            subscription.setExpiryDate(invoiceService.extendExpiryDate(startDate, cycle));
        }
        if (subscription.getNextBillingDate() == null) {
            subscription.setNextBillingDate(startDate);
        }

        subscriptionRepository.save(subscription);

        // Generate first invoice
        Invoice invoice = invoiceService.generateInvoiceForSubscription(subscription, startDate, periodEnd, "CONFIRMED");

        // Queue invoice email with PDF attachment
        emailOutboxService.queueInvoiceEmail(invoice);

        return invoice;
    }

    /**
     * Renews a subscription, creating a renewal invoice for the next period,
     * extending the expiry date, and queueing an invoice email with PDF.
     * Idempotent: repeat calls for the same period return the existing invoice.
     */
    @Transactional
    public RenewalResponseDto renewSubscription(Long subscriptionId, String currentUsername, boolean isAdminOrStaff) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        // Ownership verification for CUSTOMER role
        if (!isAdminOrStaff) {
            boolean isOwner = false;
            if (subscription.getCustomer() != null && subscription.getCustomer().getUser() != null) {
                isOwner = currentUsername.equalsIgnoreCase(subscription.getCustomer().getUser().getEmail());
            }
            if (!isOwner) {
                throw new AccessDeniedException("You do not have permission to renew subscriptions belonging to other customers");
            }
        }

        // Renewal permission check on plan version
        PlanVersion planVersion = subscription.getPlanVersion();
        if (planVersion != null && Boolean.FALSE.equals(planVersion.getAllowRenewal())) {
            throw new IllegalStateException("Plan version does not allow renewal");
        }

        String cycle = planVersion != null ? planVersion.getBillingCycle() : "MONTHLY";
        LocalDate currentExpiry = subscription.getExpiryDate() != null ? subscription.getExpiryDate() : LocalDate.now();

        // Check if a renewal invoice has already been generated for this period
        // (i.e. the latest invoice has periodEnd matching currentExpiry and is not yet replaced)
        Optional<Invoice> latestInvoiceOpt = invoiceService.findLatestInvoiceForSubscription(subscriptionId);
        if (latestInvoiceOpt.isPresent()) {
            Invoice latestInvoice = latestInvoiceOpt.get();
            if (latestInvoice.getPeriodEnd() != null && latestInvoice.getPeriodEnd().isEqual(currentExpiry)
                    && !"CANCELLED".equals(latestInvoice.getStatus())) {
                // Return existing renewal invoice (idempotent repeat call)
                emailOutboxService.queueInvoiceEmail(latestInvoice);
                return new RenewalResponseDto(
                        latestInvoice.getId(),
                        latestInvoice.getNumber(),
                        subscription.getId(),
                        subscription.getNumber(),
                        latestInvoice.getStatus(),
                        latestInvoice.getPeriodStart(),
                        latestInvoice.getPeriodEnd(),
                        latestInvoice.getDueDate(),
                        latestInvoice.getTotalPaise(),
                        MoneyUtils.formatPaiseToRupees(latestInvoice.getTotalPaise()),
                        subscription.getExpiryDate()
                );
            }
        }

        LocalDate nextPeriodStart = currentExpiry.plusDays(1);
        LocalDate nextPeriodEnd = invoiceService.calculatePeriodEnd(nextPeriodStart, cycle);

        // Idempotent invoice creation
        Invoice invoice = invoiceService.generateInvoiceForSubscription(
                subscription, nextPeriodStart, nextPeriodEnd, "CONFIRMED"
        );

        // Extend expiry date
        LocalDate newExpiry = invoiceService.extendExpiryDate(currentExpiry, cycle);
        subscription.setExpiryDate(newExpiry);
        subscription.setNextBillingDate(nextPeriodStart);
        subscriptionRepository.save(subscription);

        // Queue email with PDF attachment
        emailOutboxService.queueInvoiceEmail(invoice);

        return new RenewalResponseDto(
                invoice.getId(),
                invoice.getNumber(),
                subscription.getId(),
                subscription.getNumber(),
                invoice.getStatus(),
                invoice.getPeriodStart(),
                invoice.getPeriodEnd(),
                invoice.getDueDate(),
                invoice.getTotalPaise(),
                MoneyUtils.formatPaiseToRupees(invoice.getTotalPaise()),
                subscription.getExpiryDate()
        );
    }
}
