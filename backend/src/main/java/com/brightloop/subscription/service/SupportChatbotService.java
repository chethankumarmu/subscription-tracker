package com.brightloop.subscription.service;

import com.brightloop.subscription.dto.ChatMessageRequest;
import com.brightloop.subscription.dto.ChatMessageResponse;
import com.brightloop.subscription.model.*;
import com.brightloop.subscription.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class SupportChatbotService {

    private final FaqArticleRepository faqArticleRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SupportChatbotService(
            FaqArticleRepository faqArticleRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            SupportTicketRepository supportTicketRepository,
            TicketCommentRepository ticketCommentRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.faqArticleRepository = faqArticleRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.ticketCommentRepository = ticketCommentRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    private Long getStaffOrAdminUserId() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()) || "STAFF".equalsIgnoreCase(u.getRole()))
                .map(User::getId)
                .findFirst()
                .orElseGet(() -> userRepository.findAll().stream().findFirst().map(User::getId).orElse(null));
    }

    @Transactional
    public ChatMessageResponse processMessage(ChatMessageRequest req) {
        // Find or create session
        ChatSession session;
        if (req.getSessionId() != null) {
            session = chatSessionRepository.findById(req.getSessionId())
                    .orElseGet(() -> createNewSession(req.getCustomerId()));
        } else {
            session = createNewSession(req.getCustomerId());
        }

        // Save incoming customer message
        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(session.getId())
                .sender("CUSTOMER")
                .body(req.getMessage())
                .build());

        String text = req.getMessage().toLowerCase();
        boolean escalate = text.contains("human") || text.contains("agent") || text.contains("escalate") || text.contains("complaint") || text.contains("issue");

        String reply;
        String intent = "GENERAL_FAQ";
        String ticketNumber = null;

        if (escalate) {
            session.setStatus("ESCALATED");
            intent = "ESCALATED_TO_STAFF";

            ticketNumber = "TKT-" + (1000 + supportTicketRepository.count() + 1);
            Long custId = req.getCustomerId() != null ? req.getCustomerId() : 1L;

            SupportTicket ticket = supportTicketRepository.save(SupportTicket.builder()
                    .number(ticketNumber)
                    .customerId(custId)
                    .sessionId(session.getId())
                    .subject("Chatbot Escalation: " + (req.getMessage().length() > 60 ? req.getMessage().substring(0, 57) + "..." : req.getMessage()))
                    .status("OPEN")
                    .priority("HIGH")
                    .build());

            session.setTicketId(ticket.getId());
            chatSessionRepository.save(session);

            Long staffUserId = getStaffOrAdminUserId();
            ticketCommentRepository.save(TicketComment.builder()
                    .ticketId(ticket.getId())
                    .authorId(staffUserId != null ? staffUserId : session.getUserId())
                    .body("Escalated from chatbot session #" + session.getId() + ". User inquiry: " + req.getMessage())
                    .isInternal(true)
                    .build());

            reply = "I've escalated your conversation to our senior support staff. Your priority ticket number is " + ticketNumber + ". An agent will review this shortly in the staff portal.";
        } else {
            // Check FAQ database
            List<FaqArticle> faqs = faqArticleRepository.findByIsPublishedTrue();
            FaqArticle matchedFaq = null;

            for (FaqArticle faq : faqs) {
                if (text.contains("pause") && faq.getQuestion().toLowerCase().contains("pause")) {
                    matchedFaq = faq;
                    intent = "SUBSCRIPTION_PAUSE";
                    break;
                }
                if ((text.contains("pay") || text.contains("method") || text.contains("upi") || text.contains("card"))
                        && faq.getQuestion().toLowerCase().contains("payment")) {
                    matchedFaq = faq;
                    intent = "PAYMENT_METHODS";
                    break;
                }
                if ((text.contains("upgrade") || text.contains("change") || text.contains("plan"))
                        && faq.getQuestion().toLowerCase().contains("upgrade")) {
                    matchedFaq = faq;
                    intent = "PLAN_UPGRADE";
                    break;
                }
                if ((text.contains("invoice") || text.contains("tax") || text.contains("gst"))
                        && faq.getQuestion().toLowerCase().contains("invoice")) {
                    matchedFaq = faq;
                    intent = "TAX_INVOICE";
                    break;
                }
            }

            if (matchedFaq != null) {
                reply = matchedFaq.getAnswer();
            } else {
                reply = "I'm the Brightloop Virtual AI Assistant. I can help you check invoices, pause subscriptions, or upgrade your plan. Would you like me to connect you with a staff agent?";
            }
        }

        // Save bot reply
        chatMessageRepository.save(ChatMessage.builder()
                .sessionId(session.getId())
                .sender(escalate ? "AGENT" : "BOT")
                .body(reply)
                .intent(intent)
                .build());

        List<String> suggestions = List.of(
                "How do I pause my subscription?",
                "What payment methods are supported?",
                "Where can I find tax invoices?",
                "Connect with human agent"
        );

        return ChatMessageResponse.builder()
                .sessionId(session.getId())
                .sender(escalate ? "AGENT" : "BOT")
                .reply(reply)
                .intent(intent)
                .escalated(escalate)
                .ticketNumber(ticketNumber)
                .suggestions(suggestions)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    private ChatSession createNewSession(Long customerId) {
        Long custId = customerId != null ? customerId : 1L;
        Long userId = customerRepository.findById(custId)
                .map(Customer::getUserId)
                .orElseGet(this::getStaffOrAdminUserId);

        return chatSessionRepository.save(ChatSession.builder()
                .customerId(custId)
                .userId(userId)
                .status("BOT")
                .startedAt(OffsetDateTime.now())
                .build());
    }

    @Transactional(readOnly = true)
    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public SupportTicket updateTicketStatus(Long ticketId, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));
        ticket.setStatus(status.toUpperCase());
        if ("RESOLVED".equalsIgnoreCase(status)) {
            ticket.setResolvedAt(OffsetDateTime.now());
        }
        return supportTicketRepository.save(ticket);
    }
}
