package com.brightloop.subscription.jobs.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.jobs.dto.BillingRunResponseDto;
import com.brightloop.subscription.jobs.entity.JobRun;
import com.brightloop.subscription.jobs.repository.JobRunRepository;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingRunServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private EmailOutboxService emailOutboxService;

    @Mock
    private JobRunRepository jobRunRepository;

    private BillingSubscriptionProcessor processor;
    private BillingRunService billingRunService;

    @BeforeEach
    void setUp() {
        processor = new BillingSubscriptionProcessor(
                subscriptionRepository,
                invoiceRepository,
                invoiceService,
                emailOutboxService
        );
        billingRunService = new BillingRunService(
                subscriptionRepository,
                processor,
                jobRunRepository
        );
    }

    @Test
    void testBillingRunIdempotency_SecondRunCreatesZeroNewInvoices() {
        LocalDate runDate = LocalDate.of(2026, 11, 1);
        Long subId = 10L;

        Customer customer = Customer.builder().id(1L).contactName("Bob").build();
        PlanVersion planVersion = PlanVersion.builder().billingCycle("MONTHLY").pricePaise(200000L).build();

        Subscription subscription = Subscription.builder()
                .id(subId)
                .number("SUB-2026-000010")
                .customer(customer)
                .planVersion(planVersion)
                .status("ACTIVE")
                .nextBillingDate(runDate)
                .build();

        Invoice invoice1 = Invoice.builder()
                .id(1001L)
                .number("INV-2026-001001")
                .subscription(subscription)
                .customer(customer)
                .periodStart(runDate)
                .periodEnd(LocalDate.of(2026, 11, 30))
                .status("CONFIRMED")
                .build();

        when(jobRunRepository.save(any(JobRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subscriptionRepository.findByStatus("ACTIVE")).thenReturn(List.of(subscription));

        when(invoiceService.calculatePeriodEnd(runDate, "MONTHLY")).thenReturn(LocalDate.of(2026, 11, 30));
        // First run: no existing invoice for (subId, periodStart)
        when(invoiceRepository.findBySubscriptionIdAndPeriodStart(subId, runDate))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(invoice1)); // Second run: invoice already exists

        when(invoiceService.generateInvoiceForSubscription(eq(subscription), eq(runDate), eq(LocalDate.of(2026, 11, 30)), eq("CONFIRMED")))
                .thenReturn(invoice1);

        // --- Run 1: First billing run ---
        BillingRunResponseDto run1 = billingRunService.executeBillingRun(runDate, 1L);

        assertEquals(1, run1.getCreated());
        assertEquals(0, run1.getSkipped());
        assertEquals(0, run1.getFailed());
        verify(invoiceService, times(1)).generateInvoiceForSubscription(any(), any(), any(), any());
        verify(emailOutboxService, times(1)).queueInvoiceEmail(invoice1);

        // --- Run 2: Second billing run on same date ---
        BillingRunResponseDto run2 = billingRunService.executeBillingRun(runDate, 1L);

        // Must create 0 new invoices on the second run!
        assertEquals(0, run2.getCreated());
        assertEquals(1, run2.getSkipped());
        assertEquals(0, run2.getFailed());
        // Verify generateInvoiceForSubscription was NOT called a second time
        verify(invoiceService, times(1)).generateInvoiceForSubscription(any(), any(), any(), any());
    }

    @Test
    void testBillingRun_OneFailedSubscriptionDoesNotStopRun() {
        LocalDate runDate = LocalDate.of(2026, 11, 1);

        Subscription sub1 = Subscription.builder()
                .id(1L)
                .status("ACTIVE")
                .nextBillingDate(runDate)
                .planVersion(PlanVersion.builder().billingCycle("MONTHLY").build())
                .build();

        Subscription sub2 = Subscription.builder()
                .id(2L)
                .status("ACTIVE")
                .nextBillingDate(runDate)
                .planVersion(PlanVersion.builder().billingCycle("MONTHLY").build())
                .build();

        when(jobRunRepository.save(any(JobRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subscriptionRepository.findByStatus("ACTIVE")).thenReturn(List.of(sub1, sub2));

        when(invoiceService.calculatePeriodEnd(runDate, "MONTHLY")).thenReturn(LocalDate.of(2026, 11, 30));
        when(invoiceRepository.findBySubscriptionIdAndPeriodStart(anyLong(), eq(runDate))).thenReturn(Optional.empty());

        // sub1 fails with exception
        when(invoiceService.generateInvoiceForSubscription(eq(sub1), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error during line item calculation"));

        // sub2 succeeds
        Invoice inv2 = Invoice.builder().id(202L).number("INV-202").build();
        when(invoiceService.generateInvoiceForSubscription(eq(sub2), any(), any(), any()))
                .thenReturn(inv2);

        BillingRunResponseDto response = billingRunService.executeBillingRun(runDate, 1L);

        // 1 succeeded (created), 0 skipped, 1 failed
        assertEquals(1, response.getCreated());
        assertEquals(0, response.getSkipped());
        assertEquals(1, response.getFailed());

        // Verify JobRun was recorded with finished counts
        ArgumentCaptor<JobRun> captor = ArgumentCaptor.forClass(JobRun.class);
        verify(jobRunRepository, atLeast(2)).save(captor.capture());
        JobRun finalJobRun = captor.getValue();
        assertEquals(1, finalJobRun.getCreatedCount());
        assertEquals(0, finalJobRun.getSkippedCount());
        assertEquals(1, finalJobRun.getFailedCount());
        assertEquals("COMPLETED", finalJobRun.getStatus());
        assertNotNull(finalJobRun.getFinishedAt());
    }
}
