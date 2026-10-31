package com.brightloop.subscription.support.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.chatbot.entity.ChatMessage;
import com.brightloop.subscription.chatbot.entity.ChatSession;
import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import com.brightloop.subscription.chatbot.entity.MessageSender;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.support.dto.*;
import com.brightloop.subscription.support.entity.*;
import com.brightloop.subscription.support.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<FaqDto> getPublishedFaqs() {
        return faqArticleRepository.findByIsPublishedTrue().stream()
                .map(f -> new FaqDto(f.getId(), f.getCategory(), f.getQuestion(), f.getAnswer(), f.getIsPublished()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageResponse processMessage(ChatMessageRequest req) {
        ChatSession session;
        if (req.getSessionId() != null) {
            session = chatSessionRepository.findById(req.getSessionId())
                    .orElseGet(() -> createNewSession(req.getCustomerId()));
        } else {
            session = createNewSession(req.getCustomerId());
        }

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
    public List<SupportTicketDto> getAllTickets(UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        List<SupportTicket> tickets;
        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            Long custId = principal.getCustomerId();
            if (custId == null) {
                return List.of();
            }
            tickets = supportTicketRepository.findByCustomerIdOrderByCreatedAtDesc(custId);
        } else {
            tickets = supportTicketRepository.findAllByOrderByCreatedAtDesc();
        }

        return tickets.stream().map(t -> toTicketDto(t, principal)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportTicketDto getTicketById(Long id, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(ticket.getCustomerId())) {
                // Return 404 to avoid revealing existence of other customer tickets
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
            }
        }

        return toTicketDto(ticket, principal);
    }

    @Transactional
    public TicketCommentDto addComment(Long ticketId, String body, Boolean isInternal, UserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        boolean internalFlag = false;
        if ("CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            if (principal.getCustomerId() == null || !principal.getCustomerId().equals(ticket.getCustomerId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
            }
            // Customers CANNOT create internal notes
            internalFlag = false;
        } else {
            // ADMIN or STAFF can specify isInternal
            internalFlag = Boolean.TRUE.equals(isInternal);
        }

        TicketComment comment = ticketCommentRepository.save(TicketComment.builder()
                .ticketId(ticket.getId())
                .authorId(principal.getUserId())
                .body(body)
                .isInternal(internalFlag)
                .build());

        return new TicketCommentDto(
                comment.getId(),
                comment.getTicketId(),
                comment.getAuthorId(),
                comment.getBody(),
                comment.getIsInternal(),
                comment.getCreatedAt()
        );
    }

    @Transactional
    public SupportTicketDto updateTicketStatus(Long ticketId, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId));
        ticket.setStatus(status.toUpperCase());
        if ("RESOLVED".equalsIgnoreCase(status)) {
            ticket.setResolvedAt(OffsetDateTime.now());
        }
        SupportTicket saved = supportTicketRepository.save(ticket);
        return toTicketDto(saved, null);
    }

    private SupportTicketDto toTicketDto(SupportTicket ticket, UserPrincipal principal) {
        List<TicketComment> comments = ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId());

        // CRITICAL RULE: Internal notes (ticket_comments.is_internal) are hidden from CUSTOMER
        boolean isCustomer = principal != null && "CUSTOMER".equalsIgnoreCase(principal.getRole());

        List<TicketCommentDto> commentDtos = comments.stream()
                .filter(c -> !isCustomer || !Boolean.TRUE.equals(c.getIsInternal()))
                .map(c -> new TicketCommentDto(
                        c.getId(),
                        c.getTicketId(),
                        c.getAuthorId(),
                        c.getBody(),
                        c.getIsInternal(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new SupportTicketDto(
                ticket.getId(),
                ticket.getNumber(),
                ticket.getCustomerId(),
                ticket.getSessionId(),
                ticket.getSubject(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAssignedStaffId(),
                ticket.getCreatedAt(),
                ticket.getResolvedAt(),
                commentDtos
        );
    }
}
