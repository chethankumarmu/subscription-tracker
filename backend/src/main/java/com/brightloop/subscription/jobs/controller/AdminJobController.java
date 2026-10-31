package com.brightloop.subscription.jobs.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.jobs.dto.BillingRunResponseDto;
import com.brightloop.subscription.jobs.service.BillingRunService;
import com.brightloop.subscription.jobs.service.BillingSubscriptionProcessor;
import com.brightloop.subscription.subscriptions.job.RenewalReminderJob;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminJobController {

    private final RenewalReminderJob renewalReminderJob;
    private final BillingRunService billingRunService;

    public AdminJobController(
            RenewalReminderJob renewalReminderJob,
            BillingRunService billingRunService
    ) {
        this.renewalReminderJob = renewalReminderJob;
        this.billingRunService = billingRunService;
    }

    /**
     * Overloaded constructor for unit test compatibility.
     */
    public AdminJobController(
            RenewalReminderJob renewalReminderJob,
            SubscriptionRepository subscriptionRepository,
            InvoiceService invoiceService
    ) {
        this(renewalReminderJob, new BillingRunService(
                subscriptionRepository,
                new BillingSubscriptionProcessor(subscriptionRepository, null, invoiceService, null),
                null
        ));
    }

    /**
     * Triggers a billing run for a target date (defaults to today).
     * ADMIN only. Returns created, skipped, and failed counts.
     */
    @PostMapping({"/billing-runs", "/jobs/billing-run"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillingRunResponseDto> runBilling(
            @RequestParam(name = "date", required = false) String date,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LocalDate targetDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
        Long userId = principal != null ? principal.getUserId() : null;
        BillingRunResponseDto response = billingRunService.executeBillingRun(targetDate, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Direct method overload for backwards compatibility in existing tests.
     */
    public ResponseEntity<BillingRunResponseDto> runBilling(String date) {
        return runBilling(date, null);
    }

    /**
     * Manually triggers the renewal reminder job immediately.
     */
    @PostMapping({"/jobs/reminders", "/reminders"})
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
