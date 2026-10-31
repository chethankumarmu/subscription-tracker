package com.brightloop.subscription.subscriptions.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.subscriptions.dto.RenewalResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceRenewalTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private EmailOutboxService emailOutboxService;

    @Mock
    private com.brightloop.subscription.plans.repository.PlanVersionRepository planVersionRepository;

    @Mock
    private com.brightloop.subscription.auth.repository.CustomerRepository customerRepository;

    @Mock
    private com.brightloop.subscription.auth.repository.UserRepository userRepository;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(
                subscriptionRepository,
                invoiceService,
                emailOutboxService,
                planVersionRepository,
                customerRepository,
                userRepository
        );
    }

    @Test
    void testRenewalIdempotency_SecondCallReturnsSameInvoice() {
        Long subId = 1L;
        LocalDate currentExpiry = LocalDate.of(2026, 11, 30);
        LocalDate nextPeriodStart = LocalDate.of(2026, 12, 1);
        LocalDate nextPeriodEnd = LocalDate.of(2026, 12, 31);
        LocalDate newExpiry = LocalDate.of(2026, 12, 31);

        User user = User.builder().email("customer@brightloop.test").build();
        Customer customer = Customer.builder().user(user).contactName("Alice").build();
        Plan plan = Plan.builder().name("Pro").build();
        PlanVersion planVersion = PlanVersion.builder()
                .plan(plan)
                .billingCycle("MONTHLY")
                .allowRenewal(true)
                .pricePaise(149900L)
                .build();

        Subscription subscription = Subscription.builder()
                .id(subId)
                .number("SUB-2026-000001")
                .customer(customer)
                .planVersion(planVersion)
                .status("ACTIVE")
                .expiryDate(currentExpiry)
                .build();

        Invoice invoice1 = Invoice.builder()
                .id(501L)
                .number("INV-2026-000501")
                .subscription(subscription)
                .customer(customer)
                .status("CONFIRMED")
                .periodStart(nextPeriodStart)
                .periodEnd(nextPeriodEnd)
                .dueDate(LocalDate.of(2026, 12, 31))
                .totalPaise(149900L)
                .build();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));
        when(invoiceService.findLatestInvoiceForSubscription(subId))
                .thenReturn(Optional.empty()) // First call: no renewal invoice yet
                .thenReturn(Optional.of(invoice1)); // Second call: renewal invoice already exists
        when(invoiceService.calculatePeriodEnd(nextPeriodStart, "MONTHLY")).thenReturn(nextPeriodEnd);
        when(invoiceService.extendExpiryDate(currentExpiry, "MONTHLY")).thenReturn(newExpiry);
        when(invoiceService.generateInvoiceForSubscription(eq(subscription), eq(nextPeriodStart), eq(nextPeriodEnd), anyString()))
                .thenReturn(invoice1);

        // First renewal call
        RenewalResponseDto response1 = subscriptionService.renewSubscription(subId, "customer@brightloop.test", false);

        assertNotNull(response1);
        assertEquals(501L, response1.invoiceId());
        assertEquals("INV-2026-000501", response1.invoiceNumber());
        assertEquals("₹1,499.00", response1.totalFormatted());
        assertEquals(newExpiry, response1.newExpiryDate());

        // Verify expiryDate extended on subscription
        assertEquals(newExpiry, subscription.getExpiryDate());

        // Second renewal call for the same period returns the existing invoice
        RenewalResponseDto response2 = subscriptionService.renewSubscription(subId, "customer@brightloop.test", false);

        assertNotNull(response2);
        assertEquals(response1.invoiceId(), response2.invoiceId());
        assertEquals(response1.invoiceNumber(), response2.invoiceNumber());
        assertEquals(response1.totalPaise(), response2.totalPaise());
        assertEquals(response1.newExpiryDate(), response2.newExpiryDate());

        // Verify email queued
        verify(emailOutboxService, times(2)).queueInvoiceEmail(invoice1);
    }

    @Test
    void testRenewSubscription_DisallowedWhenAllowRenewalIsFalse() {
        Long subId = 2L;
        PlanVersion planVersion = PlanVersion.builder()
                .allowRenewal(false)
                .build();

        Subscription subscription = Subscription.builder()
                .id(subId)
                .number("SUB-NO-RENEW")
                .planVersion(planVersion)
                .build();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                subscriptionService.renewSubscription(subId, "admin@brightloop.test", true)
        );

        assertTrue(ex.getMessage().contains("Plan version does not allow renewal"));
        verifyNoInteractions(invoiceService);
        verifyNoInteractions(emailOutboxService);
    }

    @Test
    void testRenewSubscription_AccessDeniedForDifferentCustomer() {
        Long subId = 3L;
        User user = User.builder().email("owner@brightloop.test").build();
        Customer customer = Customer.builder().user(user).build();

        Subscription subscription = Subscription.builder()
                .id(subId)
                .customer(customer)
                .build();

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        // Another customer tries to renew
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                subscriptionService.renewSubscription(subId, "stranger@brightloop.test", false)
        );

        assertTrue(ex.getMessage().contains("permission to renew subscriptions belonging to other customers"));
        verifyNoInteractions(invoiceService);
        verifyNoInteractions(emailOutboxService);
    }
}
