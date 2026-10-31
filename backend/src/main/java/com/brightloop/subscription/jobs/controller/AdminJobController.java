package com.brightloop.subscription.jobs.controller;

import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.jobs.dto.BillingRunResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.job.RenewalReminderJob;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobController {

    private final RenewalReminderJob renewalReminderJob;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceService invoiceService;

    public AdminJobController(
            RenewalReminderJob renewalReminderJob,
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService
    ) {
        this.renewalReminderJob = renewalReminderJob;
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceService = invoiceService;
    }

    /**
     * Triggers a billing run for a target date (defaults to today).
     * ADMIN only. Returns created, skipped, and failed counts.
     */
    @PostMapping("/billing-run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRunResponseDto> runBilling(
            @RequestParam(required = false) String date
    ) {
        LocalDate targetDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
        int created = 0;
        int skipped = 0;
        int failed = 0;

        List<Subscription> activeSubs = subscriptionRepository.findByStatus("ACTIVE");
        for (Subscription sub : activeSubs) {
            if (sub.getNextBillingDate() != null && !sub.getNextBillingDate().isAfter(targetDate)) {
                try {
                    String cycle = sub.getPlanVersion() != null ? sub.getPlanVersion().getBillingCycle() : "MONTHLY";
                    LocalDate periodStart = sub.getNextBillingDate();
                    LocalDate periodEnd = invoiceService.calculatePeriodEnd(periodStart, cycle);
                    invoiceService.generateInvoiceForSubscription(sub, periodStart, periodEnd, "CONFIRMED");
                    sub.setNextBillingDate(periodEnd.plusDays(1));
                    subscriptionRepository.save(sub);
                    created++;
                } catch (Exception e) {
                    failed++;
                }
            } else {
                skipped++;
            }
        }

        return ResponseEntity.ok(new BillingRunResponseDto(created, skipped, failed, targetDate, "COMPLETED"));
    }

    /**
     * Manually triggers the renewal reminder job immediately.
     */
    @PostMapping("/reminders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> triggerReminders() {
        int processed = renewalReminderJob.processRemindersForDate(LocalDate.now());
        return ResponseEntity.ok(Map.of(
                "status", "COMPLETED",
                "job", "RENEWAL_REMINDERS",
                "remindersProcessed", processed,
                "executedForDate", LocalDate.now().toString()
        ));
    }
}
