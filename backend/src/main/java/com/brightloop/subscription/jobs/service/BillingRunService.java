package com.brightloop.subscription.jobs.service;

import com.brightloop.subscription.jobs.dto.BillingRunResponseDto;
import com.brightloop.subscription.jobs.entity.JobRun;
import com.brightloop.subscription.jobs.repository.JobRunRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class BillingRunService {

    private static final Logger log = LoggerFactory.getLogger(BillingRunService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final BillingSubscriptionProcessor processor;
    private final JobRunRepository jobRunRepository;

    public BillingRunService(
            SubscriptionRepository subscriptionRepository,
            BillingSubscriptionProcessor processor,
            JobRunRepository jobRunRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.processor = processor;
        this.jobRunRepository = jobRunRepository;
    }

    public BillingRunResponseDto executeBillingRun(LocalDate targetDate, Long triggeredByUserId) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        JobRun jobRun = null;
        if (jobRunRepository != null) {
            jobRun = new JobRun("BILLING", targetDate, triggeredByUserId);
            jobRun = jobRunRepository.save(jobRun);
        }

        int created = 0;
        int skipped = 0;
        int failed = 0;

        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus("ACTIVE");
        log.info("Starting billing run for date: {}. Found {} active subscriptions.", targetDate, activeSubscriptions.size());

        for (Subscription sub : activeSubscriptions) {
            try {
                BillingSubscriptionProcessor.ProcessResult result =
                        processor.processSubscriptionInTransaction(sub, targetDate);
                if (result == BillingSubscriptionProcessor.ProcessResult.CREATED) {
                    created++;
                } else if (result == BillingSubscriptionProcessor.ProcessResult.SKIPPED) {
                    skipped++;
                }
            } catch (Exception ex) {
                log.error("Failed to bill subscription id={}: {}", sub != null ? sub.getId() : "null", ex.getMessage());
                failed++;
            }
        }

        if (jobRunRepository != null && jobRun != null) {
            jobRun.setCreatedCount(created);
            jobRun.setSkippedCount(skipped);
            jobRun.setFailedCount(failed);
            jobRun.setStatus("COMPLETED");
            jobRun.setFinishedAt(OffsetDateTime.now());
            jobRunRepository.save(jobRun);
        }

        log.info("Billing run finished for {}: created={}, skipped={}, failed={}", targetDate, created, skipped, failed);
        return new BillingRunResponseDto(created, skipped, failed, targetDate, "COMPLETED");
    }
}
