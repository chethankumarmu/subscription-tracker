package com.brightloop.subscription.security;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.repository.InvoiceLineRepository;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.billing.service.InvoiceService;
import com.brightloop.subscription.catalog.controller.ProductController;
import com.brightloop.subscription.catalog.dto.CreateProductRequest;
import com.brightloop.subscription.catalog.dto.ProductAdminDto;
import com.brightloop.subscription.catalog.dto.ProductPublicDto;
import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.service.ProductService;
import com.brightloop.subscription.discounts.repository.DiscountRedemptionRepository;
import com.brightloop.subscription.jobs.controller.AdminJobController;
import com.brightloop.subscription.jobs.dto.BillingRunResponseDto;
import com.brightloop.subscription.notifications.service.EmailOutboxService;
import com.brightloop.subscription.payments.repository.PaymentRepository;
import com.brightloop.subscription.payments.service.BillingService;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.reporting.dto.StaffDashboardStatsDto;
import com.brightloop.subscription.reporting.service.DashboardService;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.job.RenewalReminderJob;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import com.brightloop.subscription.subscriptions.service.SubscriptionService;
import com.brightloop.subscription.support.dto.SupportTicketDto;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.entity.TicketComment;
import com.brightloop.subscription.support.repository.*;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.support.service.SupportChatbotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionMatrixSecurityTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private InvoiceLineRepository invoiceLineRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DiscountRedemptionRepository discountRedemptionRepository;
    @Mock
    private SupportTicketRepository supportTicketRepository;
    @Mock
    private TicketCommentRepository ticketCommentRepository;
    @Mock
    private FaqArticleRepository faqArticleRepository;
    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private EmailOutboxService emailOutboxService;
    @Mock
    private PlanVersionRepository planVersionRepository;
    @Mock
    private ProductService productService;
    @Mock
    private RenewalReminderJob renewalReminderJob;

    @Test
    void customer_cannotReadAnotherCustomersSubscription_returns404() {
        SubscriptionService subscriptionService = new SubscriptionService(
                subscriptionRepository, invoiceService, emailOutboxService,
                planVersionRepository, customerRepository, userRepository,
                supportTicketRepository, ticketCommentRepository
        );

        Customer owner = Customer.builder().id(999L).build();
        Subscription otherCustomerSub = Subscription.builder()
                .id(1L)
                .number("SUB-001")
                .customer(owner)
                .build();

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(otherCustomerSub));

        // Customer principal with customerId = 123 attempting to access subscription of customer 999
        UserPrincipal customerPrincipal = new UserPrincipal(10L, "cust@example.com", "Cust User", "CUSTOMER", 123L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                subscriptionService.getSubscriptionById(1L, customerPrincipal)
        );

        // MUST be 404 Not Found (not 403) so existence is not revealed
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void customer_cannotReadAnotherCustomersInvoice_returns404() {
        BillingService billingService = new BillingService(
                invoiceRepository, invoiceLineRepository, paymentRepository,
                customerRepository, userRepository, discountRedemptionRepository
        );

        Customer owner = Customer.builder().id(999L).build();
        Invoice otherCustomerInvoice = Invoice.builder()
                .id(101L)
                .number("INV-001")
                .customer(owner)
                .build();

        when(invoiceRepository.findById(101L)).thenReturn(Optional.of(otherCustomerInvoice));

        // Customer principal with customerId = 123 attempting to access invoice of customer 999
        UserPrincipal customerPrincipal = new UserPrincipal(10L, "cust@example.com", "Cust User", "CUSTOMER", 123L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                billingService.getInvoiceDetails(101L, customerPrincipal)
        );

        // MUST be 404 Not Found (not 403)
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void staff_dashboardResponse_hasNoMrrOrArrFields() throws Exception {
        DashboardService dashboardService = new DashboardService(
                invoiceRepository, paymentRepository, subscriptionRepository, customerRepository, supportTicketRepository
        );

        when(subscriptionRepository.findByStatus("ACTIVE")).thenReturn(List.of());
        when(invoiceRepository.count()).thenReturn(5L);
        when(supportTicketRepository.findByStatus("OPEN")).thenReturn(List.of());
        when(invoiceRepository.findByStatus(anyString())).thenReturn(List.of());

        StaffDashboardStatsDto staffStats = dashboardService.getStaffStats();

        String json = objectMapper.writeValueAsString(staffStats).toLowerCase();

        // Ensure operational counts exist
        assertTrue(json.contains("activesubscriptionscount"));
        assertTrue(json.contains("totalinvoicescount"));

        // Ensure NO financial/MRR/ARR fields exist
        assertFalse(json.contains("mrr"));
        assertFalse(json.contains("arr"));
        assertFalse(json.contains("revenue"));
        assertFalse(json.contains("spending"));
        assertFalse(json.contains("earnings"));
    }

    @Test
    void customer_responsesNeverContainCostPriceOrInternalNotes() throws Exception {
        // 1. Cost Price is hidden from public / customer product DTO
        Product product = new Product(1L, "PROD-1", "Cloud Service", "SERVICE", 50000L, 20000L, "Internal notes", true, null, null);
        ProductService prodService = new ProductService(null);
        ProductPublicDto publicDto = prodService.toPublicDto(product);

        String productJson = objectMapper.writeValueAsString(publicDto);
        assertFalse(productJson.contains("costPrice"));
        assertFalse(productJson.contains("costPricePaise"));
        assertFalse(productJson.contains("margin"));

        // 2. Internal notes on ticket comments are stripped for CUSTOMER
        SupportChatbotService supportService = new SupportChatbotService(
                faqArticleRepository, chatSessionRepository, chatMessageRepository,
                supportTicketRepository, ticketCommentRepository, customerRepository, userRepository
        );

        SupportTicket ticket = SupportTicket.builder()
                .id(50L)
                .number("TKT-50")
                .customerId(123L)
                .subject("Need help")
                .status("OPEN")
                .priority("HIGH")
                .build();

        TicketComment publicComment = TicketComment.builder()
                .id(1L)
                .ticketId(50L)
                .authorId(2L)
                .body("Hello! How can we assist you?")
                .isInternal(false)
                .build();

        TicketComment internalNote = TicketComment.builder()
                .id(2L)
                .ticketId(50L)
                .authorId(2L)
                .body("SECRET STAFF NOTE: Customer has high renewal churn risk.")
                .isInternal(true)
                .build();

        when(supportTicketRepository.findById(50L)).thenReturn(Optional.of(ticket));
        when(ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(50L)).thenReturn(List.of(publicComment, internalNote));

        UserPrincipal customerPrincipal = new UserPrincipal(10L, "cust@example.com", "Cust User", "CUSTOMER", 123L);
        SupportTicketDto customerView = supportService.getTicketById(50L, customerPrincipal);

        // Only public comment is returned to customer
        assertEquals(1, customerView.getComments().size());
        assertEquals("Hello! How can we assist you?", customerView.getComments().get(0).getBody());

        String ticketJson = objectMapper.writeValueAsString(customerView);
        assertFalse(ticketJson.contains("SECRET STAFF NOTE"));
    }

    @Test
    void admin_canRunBillingRun_andGetsCreatedSkippedFailedCounts() {
        AdminJobController adminJobController = new AdminJobController(
                renewalReminderJob, subscriptionRepository, invoiceService
        );

        Subscription subDue = Subscription.builder()
                .id(1L)
                .number("SUB-DUE")
                .status("ACTIVE")
                .nextBillingDate(LocalDate.now().minusDays(1))
                .build();

        Subscription subFuture = Subscription.builder()
                .id(2L)
                .number("SUB-FUTURE")
                .status("ACTIVE")
                .nextBillingDate(LocalDate.now().plusDays(10))
                .build();

        when(subscriptionRepository.findByStatus("ACTIVE")).thenReturn(List.of(subDue, subFuture));
        when(invoiceService.calculatePeriodEnd(any(LocalDate.class), anyString())).thenReturn(LocalDate.now().plusMonths(1));

        ResponseEntity<BillingRunResponseDto> response = adminJobController.runBilling(LocalDate.now().toString());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getCreatedCount());
        assertEquals(1, response.getBody().getSkippedCount());
        assertEquals(0, response.getBody().getFailedCount());
        assertEquals("COMPLETED", response.getBody().getStatus());
    }
}
