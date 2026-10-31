package com.brightloop.subscription.subscriptions.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.subscriptions.dto.CreateSubscriptionRequest;
import com.brightloop.subscription.subscriptions.dto.SubscriptionResponseDto;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServicePlanTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private EmailOutboxService emailOutboxService;

    @Mock
    private PlanVersionRepository planVersionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

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
    void testSubscriptionCreationRejectsDraftVersion() {
        PlanVersion draftVersion = PlanVersion.builder()
                .id(1L)
                .versionNo(1)
                .status("DRAFT")
                .pricePaise(49900L)
                .build();

        when(planVersionRepository.findById(1L)).thenReturn(Optional.of(draftVersion));

        CreateSubscriptionRequest request = new CreateSubscriptionRequest(10L, 1L, 1, LocalDate.now(), 30);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subscriptionService.createSubscription(request, "admin@brightloop.test", true)
        );

        assertTrue(ex.getMessage().contains("Cannot subscribe to an unpublished or retired plan version"));
    }

    @Test
    void testSubscriptionCreationRejectsRetiredVersion() {
        PlanVersion retiredVersion = PlanVersion.builder()
                .id(2L)
                .versionNo(1)
                .status("RETIRED")
                .effectiveTo(OffsetDateTime.now().minusDays(1))
                .pricePaise(149900L)
                .build();

        when(planVersionRepository.findById(2L)).thenReturn(Optional.of(retiredVersion));

        CreateSubscriptionRequest request = new CreateSubscriptionRequest(10L, 2L, 1, LocalDate.now(), 30);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> subscriptionService.createSubscription(request, "admin@brightloop.test", true)
        );

        assertTrue(ex.getMessage().contains("Cannot subscribe to an unpublished or retired plan version"));
    }

    @Test
    void testSubscriptionCreationCopiesPriceSnapshot() {
        Product product = Product.builder().id(5L).sku("ODOO-BASE").name("Odoo Suite").build();
        Plan plan = Plan.builder().id(100L).code("GROWTH").name("Growth Plan").product(product).build();

        PlanVersion publishedVersion = PlanVersion.builder()
                .id(3L)
                .plan(plan)
                .versionNo(2)
                .status("PUBLISHED")
                .pricePaise(159900L)
                .billingCycle("MONTHLY")
                .minQuantity(1)
                .availableFrom(LocalDate.now().minusDays(5))
                .effectiveTo(null)
                .build();

        Customer customer = Customer.builder().id(10L).contactName("Acme Corp").build();

        when(planVersionRepository.findById(3L)).thenReturn(Optional.of(publishedVersion));
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(invoiceService.extendExpiryDate(any(LocalDate.class), anyString())).thenReturn(LocalDate.now().plusMonths(1));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> {
            Subscription s = inv.getArgument(0);
            s.setId(777L);
            return s;
        });

        CreateSubscriptionRequest request = new CreateSubscriptionRequest(10L, 3L, 2, LocalDate.now(), 30);
        SubscriptionResponseDto response = subscriptionService.createSubscription(request, "admin@brightloop.test", true);

        assertNotNull(response);
        assertEquals(3L, response.getPlanVersionId());
        assertEquals("GROWTH", response.getPlanCode());
        assertEquals(319800L, response.getTotalPaise()); // 159900 * 2

        ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(captor.capture());

        Subscription saved = captor.getValue();
        assertEquals(1, saved.getLines().size());
        assertEquals(159900L, saved.getLines().get(0).getUnitPricePaise(), "Snapshot unit price must match plan version price");
        assertEquals(319800L, saved.getLines().get(0).getLineTotalPaise());
    }
}
