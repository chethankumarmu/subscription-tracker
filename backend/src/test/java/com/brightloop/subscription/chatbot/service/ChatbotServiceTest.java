package com.brightloop.subscription.chatbot.service;

import com.brightloop.subscription.billing.entity.Invoice;
import com.brightloop.subscription.billing.entity.InvoiceStatus;
import com.brightloop.subscription.billing.repository.InvoiceRepository;
import com.brightloop.subscription.chatbot.dto.ChatResponse;
import com.brightloop.subscription.chatbot.dto.FaqFact;
import com.brightloop.subscription.chatbot.dto.InvoiceFact;
import com.brightloop.subscription.chatbot.dto.SubscriptionFact;
import com.brightloop.subscription.chatbot.entity.*;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.plans.entity.Plan;
import com.brightloop.subscription.plans.entity.PlanVersion;
import com.brightloop.subscription.plans.repository.PlanRepository;
import com.brightloop.subscription.plans.repository.PlanVersionRepository;
import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionStatus;
import com.brightloop.subscription.subscriptions.repository.SubscriptionRepository;
import com.brightloop.subscription.support.entity.FaqArticle;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;
import com.brightloop.subscription.support.repository.FaqArticleRepository;
import com.brightloop.subscription.support.service.SupportTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatbotServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private IntentDetectorService intentDetector;

    @Mock
    private CustomerFactService customerFactService;

    @Mock
    private OllamaClientService ollamaClientService;

    @Mock
    private SupportTicketService supportTicketService;

    private ChatbotService chatbotService;

    private ChatSession activeSession;

    @BeforeEach
    void setUp() {
        chatbotService = new ChatbotService(
                sessionRepository,
                messageRepository,
                intentDetector,
                customerFactService,
                ollamaClientService,
                supportTicketService
        );

        activeSession = ChatSession.builder()
                .id(100L)
                .customerId(42L)
                .userId(1L)
                .status(ChatSessionStatus.BOT)
                .build();
    }

    @Test
    @DisplayName("Invoice Status Intent: returns verified invoice facts and uses deterministic template if Ollama fails")
    void testInvoiceStatusWithOllamaFallback() {
        when(sessionRepository.findByIdAndCustomerId(100L, 42L)).thenReturn(Optional.of(activeSession));
        when(intentDetector.detectIntent(anyString())).thenReturn(ChatIntent.INVOICE_STATUS);

        InvoiceFact invoiceFact = InvoiceFact.builder()
                .number("INV-2026-0001")
                .status("PAID")
                .totalPaise(49900L)
                .totalFormatted("₹499.00")
                .dueDate(LocalDate.of(2026, 11, 15))
                .build();

        when(customerFactService.getLatestInvoice(42L)).thenReturn(Optional.of(invoiceFact));
        when(ollamaClientService.generateAnswer(anyString(), anyString())).thenReturn(Optional.empty()); // simulate Ollama timeout/failure

        ChatResponse response = chatbotService.processCustomerMessage(100L, 42L, "Show my invoice status");

        assertNotNull(response);
        assertEquals(ChatIntent.INVOICE_STATUS, response.getIntent());
        assertEquals("FACTUAL", response.getType());
        assertFalse(response.isEscalated());
        assertNotNull(response.getData().getInvoice());
        assertEquals("INV-2026-0001", response.getData().getInvoice().getNumber());
        assertTrue(response.getAnswer().contains("INV-2026-0001"));
        assertTrue(response.getAnswer().contains("₹499.00"));
        assertTrue(response.getAnswer().contains("PAID"));

        verify(messageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Next Billing Date Intent: returns verified next billing date")
    void testNextBillingDate() {
        when(sessionRepository.findByIdAndCustomerId(100L, 42L)).thenReturn(Optional.of(activeSession));
        when(intentDetector.detectIntent(anyString())).thenReturn(ChatIntent.NEXT_BILLING_DATE);

        SubscriptionFact subFact = SubscriptionFact.builder()
                .number("SUB-2026-0010")
                .planName("Pro Suite")
                .status("ACTIVE")
                .nextBillingDate(LocalDate.of(2026, 12, 1))
                .build();

        when(customerFactService.getLatestSubscription(42L)).thenReturn(Optional.of(subFact));
        when(ollamaClientService.generateAnswer(anyString(), anyString())).thenReturn(Optional.empty());

        ChatResponse response = chatbotService.processCustomerMessage(100L, 42L, "When is my next billing date?");

        assertNotNull(response);
        assertEquals(ChatIntent.NEXT_BILLING_DATE, response.getIntent());
        assertEquals("FACTUAL", response.getType());
        assertNotNull(response.getData().getSubscription());
        assertEquals(LocalDate.of(2026, 12, 1), response.getData().getSubscription().getNextBillingDate());
        assertTrue(response.getAnswer().contains("2026-12-01"));
    }

    @Test
    @DisplayName("Speak to Human Intent: auto escalates to ticket with OPEN status and MEDIUM priority")
    void testSpeakToHumanEscalation() {
        when(sessionRepository.findByIdAndCustomerId(100L, 42L)).thenReturn(Optional.of(activeSession));
        when(intentDetector.detectIntent(anyString())).thenReturn(ChatIntent.SPEAK_TO_HUMAN);

        SupportTicket createdTicket = SupportTicket.builder()
                .id(777L)
                .number("TKT-000777")
                .customerId(42L)
                .sessionId(100L)
                .status(TicketStatus.OPEN)
                .priority(TicketPriority.MEDIUM)
                .subject("Chat escalation: I need human help")
                .build();

        when(supportTicketService.escalateChatSession(eq(100L), eq(42L), anyString())).thenReturn(createdTicket);

        ChatResponse response = chatbotService.processCustomerMessage(100L, 42L, "I need human help");

        assertNotNull(response);
        assertEquals(ChatIntent.SPEAK_TO_HUMAN, response.getIntent());
        assertEquals("ESCALATION", response.getType());
        assertTrue(response.isEscalated());
        assertNotNull(response.getData().getTicket());
        assertEquals("TKT-000777", response.getData().getTicket().getNumber());
        assertEquals(TicketPriority.MEDIUM, response.getData().getTicket().getPriority());
        assertEquals(TicketStatus.OPEN, response.getData().getTicket().getStatus());
        assertTrue(response.getAnswer().contains("TKT-000777"));
    }

    @Test
    @DisplayName("Subscription Pause Intent: does NOT directly mutate subscription, provides staff guidance")
    void testSubscriptionPauseSafety() {
        when(sessionRepository.findByIdAndCustomerId(100L, 42L)).thenReturn(Optional.of(activeSession));
        when(intentDetector.detectIntent(anyString())).thenReturn(ChatIntent.SUBSCRIPTION_PAUSE);

        SubscriptionFact subFact = SubscriptionFact.builder()
                .number("SUB-2026-0010")
                .planName("Pro Suite")
                .status("ACTIVE")
                .paused(false)
                .allowPause(true)
                .build();

        when(customerFactService.getLatestSubscription(42L)).thenReturn(Optional.of(subFact));

        ChatResponse response = chatbotService.processCustomerMessage(100L, 42L, "Can I pause my subscription?");

        assertNotNull(response);
        assertEquals(ChatIntent.SUBSCRIPTION_PAUSE, response.getIntent());
        assertEquals("ACTION_REQUEST", response.getType());
        assertFalse(response.isEscalated());
        assertTrue(response.getAnswer().contains("chatbot cannot directly modify subscriptions"));
        assertTrue(response.getAnswer().contains("human support"));
    }
}
