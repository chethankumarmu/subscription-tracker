package com.brightloop.subscription.subscriptions.job;

import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionReminder;
import com.brightloop.subscription.subscriptions.repository.SubscriptionReminderRepository;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class RenewalReminderJob {

    private static final Logger log = LoggerFactory.getLogger(RenewalReminderJob.class);

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionReminderRepository subscriptionReminderRepository;
    private final EmailOutboxService emailOutboxService;

    public RenewalReminderJob(
            SubscriptionRepository subscriptionRepository,
            SubscriptionReminderRepository subscriptionReminderRepository,
            EmailOutboxService emailOutboxService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionReminderRepository = subscriptionReminderRepository;
        this.emailOutboxService = emailOutboxService;
    }

    /**
     * Hourly scheduled job checking for active subscriptions expiring in 7 or 1 days.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void runHourlyReminders() {
        LocalDate today = LocalDate.now();
        int sentCount = processRemindersForDate(today);
        log.info("Hourly renewal reminder run completed for {}. Dispatched {} reminder(s).", today, sentCount);
    }

    /**
     * Finds active subscriptions whose expiry_date is exactly 7 or 1 days from today
     * and that have no reminder for that day count. Inserts reminder row and outbox row
     * in one transaction per reminder.
     *
     * @param today Anchor date to evaluate from
     * @return Number of reminders processed
     */
    public int processRemindersForDate(LocalDate today) {
        LocalDate target7Days = today.plusDays(7);
        LocalDate target1Day = today.plusDays(1);

        List<Subscription> expiringSubscriptions = subscriptionRepository.findActiveExpiringOn(target7Days, target1Day);

        int count = 0;
        for (Subscription sub : expiringSubscriptions) {
            if (sub.getExpiryDate() == null) {
                continue;
            }
            int daysBefore = (int) ChronoUnit.DAYS.between(today, sub.getExpiryDate());
            if (daysBefore != 7 && daysBefore != 1) {
                continue;
            }

            boolean dispatched = sendReminderIfAbsent(sub, daysBefore);
            if (dispatched) {
                count++;
            }
        }
        return count;
    }

    @Transactional
    public boolean sendReminderIfAbsent(Subscription sub, int daysBefore) {
        if (subscriptionReminderRepository.existsBySubscriptionIdAndDaysBefore(sub.getId(), daysBefore)) {
            return false;
        }

        // Insert reminder tracking record
        SubscriptionReminder reminder = SubscriptionReminder.builder()
                .subscriptionId(sub.getId())
                .daysBefore(daysBefore)
                .sentAt(OffsetDateTime.now())
                .build();
        subscriptionReminderRepository.save(reminder);

        // Queue reminder in email outbox in same transaction
        emailOutboxService.queueRenewalReminderEmail(sub, daysBefore);

        log.info("Queued renewal reminder for subscription {} ({} days before expiry)", sub.getNumber(), daysBefore);
        return true;
    }
}
