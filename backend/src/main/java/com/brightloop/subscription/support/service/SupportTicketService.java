package com.brightloop.subscription.support.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.chatbot.entity.ChatMessage;
import com.brightloop.subscription.chatbot.entity.ChatSession;
import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.common.exception.ResourceNotFoundException;
import com.brightloop.subscription.support.dto.*;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.entity.TicketComment;
import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;
import com.brightloop.subscription.support.repository.SupportTicketRepository;
import com.brightloop.subscription.support.repository.TicketCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SupportTicketService(
            SupportTicketRepository ticketRepository,
            TicketCommentRepository commentRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SupportTicket escalateChatSession(Long sessionId, Long customerId, String subject) {
        ChatSession session = chatSessionRepository.findByIdAndCustomerId(sessionId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + sessionId));

        if (session.getTicketId() != null) {
            return ticketRepository.findById(session.getTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + session.getTicketId()));
        }

        String ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String ticketSubject = (subject != null && !subject.isBlank()) ? subject : "Customer requested human assistance";

        SupportTicket ticket = SupportTicket.builder()
                .number(ticketNumber)
                .customerId(customerId)
                .sessionId(sessionId)
                .subject(ticketSubject)
                .status("OPEN")
                .priority(TicketPriority.MEDIUM)
                .createdAt(OffsetDateTime.now())
                .build();

        SupportTicket savedTicket = ticketRepository.save(ticket);

        session.setStatus(ChatSessionStatus.ESCALATED);
        session.setTicketId(savedTicket.getId());
        chatSessionRepository.save(session);

        return savedTicket;
    }

    @Transactional(readOnly = true)
    public Page<TicketSummaryResponse> listTickets(TicketStatus status, Pageable pageable) {
        Page<SupportTicket> page;
        if (status != null) {
            page = ticketRepository.findAllByStatus(status.name(), pageable);
        } else {
            page = ticketRepository.findAll(pageable);
        }
        return page.map(this::mapToSummary);
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicketDetail(Long ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        Customer customer = customerRepository.findById(ticket.getCustomerId()).orElse(null);
        User staff = ticket.getAssignedStaffId() != null
                ? userRepository.findById(ticket.getAssignedStaffId()).orElse(null)
                : null;

        List<ChatTranscriptMessageDto> transcript = List.of();
        if (ticket.getSessionId() != null) {
            List<ChatMessage> messages = chatMessageRepository.findAllBySessionIdOrderByCreatedAtAsc(ticket.getSessionId());
            transcript = messages.stream()
                    .map(m -> ChatTranscriptMessageDto.builder()
                            .id(m.getId())
                            .sender(m.getSender() != null ? m.getSender().name() : "USER")
                            .body(m.getBody())
                            .intent(m.getIntent())
                            .createdAt(m.getCreatedAt())
                            .build())
                    .toList();
        }

        List<TicketComment> comments = commentRepository.findAllByTicketIdOrderByCreatedAtAsc(ticketId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(this::mapToCommentResponse)
                .toList();

        TicketStatus tStatus = null;
        try { tStatus = TicketStatus.valueOf(ticket.getStatus()); } catch (Exception ignored) {}
        TicketPriority tPriority = null;
        try { tPriority = TicketPriority.valueOf(ticket.getPriority()); } catch (Exception ignored) {}

        Instant createdInstant = ticket.getCreatedAt() != null ? ticket.getCreatedAt().toInstant() : null;
        Instant resolvedInstant = ticket.getResolvedAt() != null ? ticket.getResolvedAt().toInstant() : null;

        return TicketDetailResponse.builder()
                .id(ticket.getId())
                .number(ticket.getNumber())
                .customerId(ticket.getCustomerId())
                .companyName(customer != null ? customer.getCompanyName() : "Unknown")
                .contactName(customer != null ? customer.getContactName() : "Unknown")
                .sessionId(ticket.getSessionId())
                .subject(ticket.getSubject())
                .status(tStatus)
                .priority(tPriority)
                .assignedStaffId(ticket.getAssignedStaffId())
                .assignedStaffName(staff != null ? staff.getFullName() : null)
                .createdAt(createdInstant)
                .resolvedAt(resolvedInstant)
                .chatTranscript(transcript)
                .comments(commentResponses)
                .build();
    }

    @Transactional
    public TicketSummaryResponse updateTicketStatus(Long ticketId, UpdateTicketStatusRequest request) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus().name());
            if (request.getStatus() == TicketStatus.RESOLVED && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(OffsetDateTime.now());
            }
        }

        if (request.getAssignedStaffId() != null) {
            userRepository.findById(request.getAssignedStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found: " + request.getAssignedStaffId()));
            ticket.setAssignedStaffId(request.getAssignedStaffId());
        }

        SupportTicket saved = ticketRepository.save(ticket);
        return mapToSummary(saved);
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, Long authorId, AddCommentRequest request) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        TicketComment comment = TicketComment.builder()
                .ticketId(ticket.getId())
                .authorId(authorId)
                .body(request.getBody())
                .isInternal(request.isInternal())
                .createdAt(OffsetDateTime.now())
                .build();

        TicketComment saved = commentRepository.save(comment);
        return mapToCommentResponse(saved);
    }

    private TicketSummaryResponse mapToSummary(SupportTicket ticket) {
        Customer customer = customerRepository.findById(ticket.getCustomerId()).orElse(null);
        User staff = ticket.getAssignedStaffId() != null
                ? userRepository.findById(ticket.getAssignedStaffId()).orElse(null)
                : null;

        TicketStatus tStatus = null;
        try { tStatus = TicketStatus.valueOf(ticket.getStatus()); } catch (Exception ignored) {}
        TicketPriority tPriority = null;
        try { tPriority = TicketPriority.valueOf(ticket.getPriority()); } catch (Exception ignored) {}

        Instant createdInstant = ticket.getCreatedAt() != null ? ticket.getCreatedAt().toInstant() : null;
        Instant resolvedInstant = ticket.getResolvedAt() != null ? ticket.getResolvedAt().toInstant() : null;

        return TicketSummaryResponse.builder()
                .id(ticket.getId())
                .number(ticket.getNumber())
                .customerId(ticket.getCustomerId())
                .companyName(customer != null ? customer.getCompanyName() : "Unknown")
                .contactName(customer != null ? customer.getContactName() : "Unknown")
                .sessionId(ticket.getSessionId())
                .subject(ticket.getSubject())
                .status(tStatus)
                .priority(tPriority)
                .assignedStaffId(ticket.getAssignedStaffId())
                .assignedStaffName(staff != null ? staff.getFullName() : null)
                .createdAt(createdInstant)
                .resolvedAt(resolvedInstant)
                .build();
    }

    private CommentResponse mapToCommentResponse(TicketComment comment) {
        User author = userRepository.findById(comment.getAuthorId()).orElse(null);
        Instant createdInstant = comment.getCreatedAt() != null ? comment.getCreatedAt().toInstant() : null;
        return CommentResponse.builder()
                .id(comment.getId())
                .ticketId(comment.getTicketId())
                .authorId(comment.getAuthorId())
                .authorName(author != null ? author.getFullName() : "Staff Member")
                .body(comment.getBody())
                .internal(Boolean.TRUE.equals(comment.getIsInternal()))
                .createdAt(createdInstant)
                .build();
    }
}
