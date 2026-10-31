package com.brightloop.subscription.jobs.controller;

import com.brightloop.subscription.subscriptions.job.RenewalReminderJob;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobController {

    private final RenewalReminderJob renewalReminderJob;

    public AdminJobController(RenewalReminderJob renewalReminderJob) {
        this.renewalReminderJob = renewalReminderJob;
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
