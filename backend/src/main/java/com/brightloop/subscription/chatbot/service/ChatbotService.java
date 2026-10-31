package com.brightloop.subscription.chatbot.service;

import com.brightloop.subscription.chatbot.dto.*;
import com.brightloop.subscription.chatbot.entity.*;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.common.exception.ResourceNotFoundException;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.service.SupportTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatbotService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final IntentDetectorService intentDetector;
    private final CustomerFactService customerFactService;
    private final OllamaClientService ollamaClientService;
    private final SupportTicketService supportTicketService;

    public ChatbotService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository,
            IntentDetectorService intentDetector,
            CustomerFactService customerFactService,
            OllamaClientService ollamaClientService,
            SupportTicketService supportTicketService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.intentDetector = intentDetector;
        this.customerFactService = customerFactService;
        this.ollamaClientService = ollamaClientService;
        this.supportTicketService = supportTicketService;
    }

    @Transactional
    public ChatSession createOrGetActiveSession(Long userId, Long customerId) {
        return sessionRepository.findFirstByCustomerIdAndStatusOrderByStartedAtDesc(customerId, ChatSessionStatus.BOT)
                .orElseGet(() -> {
                    ChatSession newSession = ChatSession.builder()
                            .userId(userId)
                            .customerId(customerId)
                            .status(ChatSessionStatus.BOT)
                            .startedAt(Instant.now())
                            .build();
                    return sessionRepository.save(newSession);
                });
    }

    @Transactional(readOnly = true)
    public ChatSessionDetailResponse getSessionHistory(Long sessionId, Long customerId) {
        ChatSession session = sessionRepository.findByIdAndCustomerId(sessionId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + sessionId));

        List<ChatMessage> messages = messageRepository.findAllBySessionIdOrderByCreatedAtAsc(sessionId);
        List<ChatMessageDto> messageDtos = messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .sender(m.getSender().name())
                        .body(m.getBody())
                        .intent(m.getIntent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();

        return ChatSessionDetailResponse.builder()
                .sessionId(session.getId())
                .customerId(session.getCustomerId())
                .status(session.getStatus())
                .ticketId(session.getTicketId())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .messages(messageDtos)
                .build();
    }

    @Transactional
    public ChatResponse processCustomerMessage(Long sessionId, Long customerId, String rawMessage) {
        ChatSession session = sessionRepository.findByIdAndCustomerId(sessionId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + sessionId));

        ChatIntent detectedIntent = intentDetector.detectIntent(rawMessage);

        ChatMessage userMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .sender(MessageSender.CUSTOMER)
                .body(rawMessage)
                .intent(detectedIntent.name())
                .createdAt(Instant.now())
                .build();
        messageRepository.save(userMessage);

        ChatResponse response;
        switch (detectedIntent) {
            case SPEAK_TO_HUMAN -> response = handleSpeakToHuman(session, customerId, rawMessage);
            case INVOICE_STATUS -> response = handleInvoiceStatus(sessionId, customerId, rawMessage);
            case NEXT_BILLING_DATE -> response = handleNextBillingDate(sessionId, customerId, rawMessage);
            case SUBSCRIPTION_DETAILS -> response = handleSubscriptionDetails(sessionId, customerId, rawMessage);
            case SUBSCRIPTION_PAUSE -> response = handleSubscriptionPause(sessionId, customerId, rawMessage);
            case FAQ_QUERY -> response = handleFaqQuery(sessionId, rawMessage);
            default -> response = handleUnknown(sessionId, rawMessage);
        }

        ChatMessage botMessage = ChatMessage.builder()
                .sessionId(sessionId)
                .sender(MessageSender.BOT)
                .body(response.getAnswer())
                .intent(response.getIntent().name())
                .createdAt(Instant.now())
                .build();
        messageRepository.save(botMessage);

        return response;
    }

    private ChatResponse handleSpeakToHuman(ChatSession session, Long customerId, String customerQuery) {
        String ticketSubject = "Chat escalation: " + (customerQuery.length() > 50 ? customerQuery.substring(0, 47) + "..." : customerQuery);
        SupportTicket ticket = supportTicketService.escalateChatSession(session.getId(), customerId, ticketSubject);

        TicketFact ticketFact = TicketFact.builder()
                .id(ticket.getId())
                .number(ticket.getNumber())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .subject(ticket.getSubject())
                .build();

        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .ticket(ticketFact)
                .build();

        String answer = "I have escalated your request to our support team. Support Ticket " +
                ticket.getNumber() + " has been created with priority " + ticket.getPriority() +
                ". Our staff has your full chat transcript and will follow up shortly.";

        return ChatResponse.builder()
                .sessionId(session.getId())
                .intent(ChatIntent.SPEAK_TO_HUMAN)
                .type("ESCALATION")
                .answer(answer)
                .data(data)
                .escalated(true)
                .suggestions(List.of("View ticket details", "Ask another question"))
                .build();
    }

    private ChatResponse handleInvoiceStatus(Long sessionId, Long customerId, String userQuery) {
        Optional<InvoiceFact> invoiceOpt = customerFactService.getLatestInvoice(customerId);

        if (invoiceOpt.isEmpty()) {
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .intent(ChatIntent.INVOICE_STATUS)
                    .type("FACTUAL")
                    .answer("You currently do not have any invoices on record.")
                    .data(ChatbotDataPayload.builder().build())
                    .escalated(false)
                    .suggestions(List.of("Subscription details", "Speak to human agent"))
                    .build();
        }

        InvoiceFact invoice = invoiceOpt.get();
        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .invoice(invoice)
                .build();

        String deterministicAnswer = String.format(
                "Your latest invoice %s for %s is %s. Due date is %s.",
                invoice.getNumber(), invoice.getTotalFormatted(), invoice.getStatus(),
                invoice.getDueDate() != null ? invoice.getDueDate() : "not set");

        String systemPrompt = String.format(
                "You are Brightloop's billing assistant. Use ONLY these authoritative facts: " +
                "Invoice Number: %s, Amount: %s, Status: %s, Due Date: %s. " +
                "Never invent or alter financial amounts or dates. Be concise and polite.",
                invoice.getNumber(), invoice.getTotalFormatted(), invoice.getStatus(), invoice.getDueDate());

        String finalAnswer = ollamaClientService.generateAnswer(systemPrompt, userQuery)
                .orElse(deterministicAnswer);

        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.INVOICE_STATUS)
                .type("FACTUAL")
                .answer(finalAnswer)
                .data(data)
                .escalated(false)
                .suggestions(List.of("When is my next billing date?", "Subscription details", "Speak to human agent"))
                .build();
    }

    private ChatResponse handleNextBillingDate(Long sessionId, Long customerId, String userQuery) {
        Optional<SubscriptionFact> subOpt = customerFactService.getLatestSubscription(customerId);

        if (subOpt.isEmpty()) {
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .intent(ChatIntent.NEXT_BILLING_DATE)
                    .type("FACTUAL")
                    .answer("No active subscription found for your account.")
                    .data(ChatbotDataPayload.builder().build())
                    .escalated(false)
                    .suggestions(List.of("Speak to human agent"))
                    .build();
        }

        SubscriptionFact sub = subOpt.get();
        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .subscription(sub)
                .build();

        String deterministicAnswer;
        if (sub.getNextBillingDate() != null) {
            deterministicAnswer = String.format(
                    "Your next billing date for subscription %s (%s) is %s.",
                    sub.getNumber(), sub.getPlanName(), sub.getNextBillingDate());
        } else {
            deterministicAnswer = String.format(
                    "Your subscription %s (%s) does not have a scheduled next billing date.",
                    sub.getNumber(), sub.getPlanName());
        }

        String systemPrompt = String.format(
                "You are Brightloop's billing assistant. Use ONLY these authoritative facts: " +
                "Subscription Number: %s, Plan: %s, Next Billing Date: %s, Status: %s. " +
                "Never invent dates. Be concise and polite.",
                sub.getNumber(), sub.getPlanName(), sub.getNextBillingDate(), sub.getStatus());

        String finalAnswer = ollamaClientService.generateAnswer(systemPrompt, userQuery)
                .orElse(deterministicAnswer);

        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.NEXT_BILLING_DATE)
                .type("FACTUAL")
                .answer(finalAnswer)
                .data(data)
                .escalated(false)
                .suggestions(List.of("Check invoice status", "Subscription details", "Speak to human agent"))
                .build();
    }

    private ChatResponse handleSubscriptionDetails(Long sessionId, Long customerId, String userQuery) {
        Optional<SubscriptionFact> subOpt = customerFactService.getLatestSubscription(customerId);

        if (subOpt.isEmpty()) {
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .intent(ChatIntent.SUBSCRIPTION_DETAILS)
                    .type("FACTUAL")
                    .answer("No active subscription records were found for your account.")
                    .data(ChatbotDataPayload.builder().build())
                    .escalated(false)
                    .suggestions(List.of("Speak to human agent"))
                    .build();
        }

        SubscriptionFact sub = subOpt.get();
        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .subscription(sub)
                .build();

        String deterministicAnswer = String.format(
                "You are subscribed to %s at %s per %s. Subscription status is %s%s.",
                sub.getPlanName(), sub.getPriceFormatted(), sub.getBillingCycle().toLowerCase(),
                sub.getStatus(), sub.isPaused() ? " (Currently Paused)" : "");

        String systemPrompt = String.format(
                "You are Brightloop's subscription assistant. Use ONLY these authoritative facts: " +
                "Plan Name: %s, Price: %s, Cycle: %s, Status: %s, Paused: %s. " +
                "Never invent pricing. Be concise and polite.",
                sub.getPlanName(), sub.getPriceFormatted(), sub.getBillingCycle(), sub.getStatus(), sub.isPaused());

        String finalAnswer = ollamaClientService.generateAnswer(systemPrompt, userQuery)
                .orElse(deterministicAnswer);

        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.SUBSCRIPTION_DETAILS)
                .type("FACTUAL")
                .answer(finalAnswer)
                .data(data)
                .escalated(false)
                .suggestions(List.of("When is my next billing date?", "Check invoice status", "Pause subscription"))
                .build();
    }

    private ChatResponse handleSubscriptionPause(Long sessionId, Long customerId, String userQuery) {
        Optional<SubscriptionFact> subOpt = customerFactService.getLatestSubscription(customerId);

        if (subOpt.isEmpty()) {
            return ChatResponse.builder()
                    .sessionId(sessionId)
                    .intent(ChatIntent.SUBSCRIPTION_PAUSE)
                    .type("ACTION_REQUEST")
                    .answer("No active subscription found to pause.")
                    .data(ChatbotDataPayload.builder().build())
                    .escalated(false)
                    .suggestions(List.of("Speak to human agent"))
                    .build();
        }

        SubscriptionFact sub = subOpt.get();
        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .subscription(sub)
                .build();

        String deterministicAnswer;
        if (sub.isPaused()) {
            deterministicAnswer = String.format(
                    "Your subscription %s (%s) is already paused. To unpause or resume billing, please contact our support staff.",
                    sub.getNumber(), sub.getPlanName());
        } else if (sub.isAllowPause()) {
            deterministicAnswer = String.format(
                    "Your plan '%s' permits subscription pausing. Note that the chatbot cannot directly modify subscriptions. " +
                    "Would you like me to request our human support team to pause subscription %s for you?",
                    sub.getPlanName(), sub.getNumber());
        } else {
            deterministicAnswer = String.format(
                    "Your current plan '%s' does not permit pausing. If you need special assistance, please connect with our human support team.",
                    sub.getPlanName());
        }

        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.SUBSCRIPTION_PAUSE)
                .type("ACTION_REQUEST")
                .answer(deterministicAnswer)
                .data(data)
                .escalated(false)
                .suggestions(List.of("Speak to human agent", "Subscription details", "Back to main menu"))
                .build();
    }

    private ChatResponse handleFaqQuery(Long sessionId, String userQuery) {
        Optional<FaqFact> faqOpt = customerFactService.searchFaq(userQuery);

        if (faqOpt.isEmpty()) {
            return handleUnknown(sessionId, userQuery);
        }

        FaqFact faq = faqOpt.get();
        ChatbotDataPayload data = ChatbotDataPayload.builder()
                .faq(faq)
                .build();

        String deterministicAnswer = faq.getAnswer();

        String systemPrompt = String.format(
                "You are Brightloop's customer support assistant. Answer the user's question using ONLY the following FAQ answer. " +
                "Do not make up facts. FAQ: %s",
                faq.getAnswer());

        String finalAnswer = ollamaClientService.generateAnswer(systemPrompt, userQuery)
                .orElse(deterministicAnswer);

        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.FAQ_QUERY)
                .type("FAQ")
                .answer(finalAnswer)
                .data(data)
                .escalated(false)
                .suggestions(List.of("Check invoice status", "Subscription details", "Speak to human agent"))
                .build();
    }

    private ChatResponse handleUnknown(Long sessionId, String userQuery) {
        return ChatResponse.builder()
                .sessionId(sessionId)
                .intent(ChatIntent.UNKNOWN)
                .type("FALLBACK")
                .answer("I'm sorry, I couldn't understand that. You can ask about your latest invoice, next billing date, subscription details, or choose to speak to a human support agent.")
                .data(ChatbotDataPayload.builder().build())
                .escalated(false)
                .suggestions(List.of("Check invoice status", "When is my next billing date?", "Subscription details", "Speak to human agent"))
                .build();
    }
}

