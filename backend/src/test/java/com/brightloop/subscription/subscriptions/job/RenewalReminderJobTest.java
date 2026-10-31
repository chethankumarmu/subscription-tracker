package com.brightloop.subscription.subscriptions.job;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionReminder;
import com.brightloop.subscription.subscriptions.repository.SubscriptionReminderRepository;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenewalReminderJobTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionReminderRepository subscriptionReminderRepository;

    @Mock
    private EmailOutboxService emailOutboxService;

    private RenewalReminderJob renewalReminderJob;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        renewalReminderJob = new RenewalReminderJob(
                subscriptionRepository,
                subscriptionReminderRepository,
                emailOutboxService
        );
        today = LocalDate.of(2026, 10, 31);
    }

    @Test
    void testReminderDaySelection_DispatchesOnlyFor7And1Days() {
        LocalDate date7 = today.plusDays(7);
        LocalDate date1 = today.plusDays(1);

        User user = User.builder().email("customer@brightloop.test").build();
        Customer customer = Customer.builder().user(user).contactName("Jane Doe").build();
        Plan plan = Plan.builder().name("Pro Plan").build();
        PlanVersion planVersion = PlanVersion.builder().plan(plan).build();

        Subscription sub7Days = Subscription.builder()
                .id(101L)
                .number("SUB-7DAYS")
                .status("ACTIVE")
                .customer(customer)
                .planVersion(planVersion)
                .expiryDate(date7)
                .build();

        Subscription sub1Day = Subscription.builder()
                .id(102L)
                .number("SUB-1DAY")
                .status("ACTIVE")
                .customer(customer)
                .planVersion(planVersion)
                .expiryDate(date1)
                .build();

        // The query returns subscriptions matching date7 and date1
        when(subscriptionRepository.findActiveExpiringOn(date7, date1))
                .thenReturn(List.of(sub7Days, sub1Day));

        when(subscriptionReminderRepository.existsBySubscriptionIdAndDaysBefore(101L, 7)).thenReturn(false);
        when(subscriptionReminderRepository.existsBySubscriptionIdAndDaysBefore(102L, 1)).thenReturn(false);

        int processed = renewalReminderJob.processRemindersForDate(today);

        assertEquals(2, processed);
        verify(subscriptionReminderRepository).save(argThat(r -> r.getSubscriptionId().equals(101L) && r.getDaysBefore() == 7));
        verify(subscriptionReminderRepository).save(argThat(r -> r.getSubscriptionId().equals(102L) && r.getDaysBefore() == 1));
        verify(emailOutboxService).queueRenewalReminderEmail(sub7Days, 7);
        verify(emailOutboxService).queueRenewalReminderEmail(sub1Day, 1);
    }

    @Test
    void testReminderDaySelection_IgnoresOtherDays() {
        LocalDate date7 = today.plusDays(7);
        LocalDate date1 = today.plusDays(1);

        Subscription sub3Days = Subscription.builder()
                .id(103L)
                .number("SUB-3DAYS")
                .status("ACTIVE")
                .expiryDate(today.plusDays(3))
                .build();

        Subscription sub8Days = Subscription.builder()
                .id(104L)
                .number("SUB-8DAYS")
                .status("ACTIVE")
                .expiryDate(today.plusDays(8))
                .build();

        when(subscriptionRepository.findActiveExpiringOn(date7, date1))
                .thenReturn(List.of(sub3Days, sub8Days));

        int processed = renewalReminderJob.processRemindersForDate(today);

        assertEquals(0, processed);
        verifyNoInteractions(subscriptionReminderRepository);
        verifyNoInteractions(emailOutboxService);
    }

    @Test
    void testReminder_DoesNotDuplicateIfAlreadySent() {
        LocalDate date7 = today.plusDays(7);
        LocalDate date1 = today.plusDays(1);

        Subscription sub7Days = Subscription.builder()
                .id(105L)
                .number("SUB-ALREADY-SENT")
                .status("ACTIVE")
                .expiryDate(date7)
                .build();

        when(subscriptionRepository.findActiveExpiringOn(date7, date1))
                .thenReturn(List.of(sub7Days));
        // Already recorded
        when(subscriptionReminderRepository.existsBySubscriptionIdAndDaysBefore(105L, 7)).thenReturn(true);

        int processed = renewalReminderJob.processRemindersForDate(today);

        assertEquals(0, processed);
        verify(subscriptionReminderRepository, never()).save(any(SubscriptionReminder.class));
        verify(emailOutboxService, never()).queueRenewalReminderEmail(any(), anyInt());
    }
}
