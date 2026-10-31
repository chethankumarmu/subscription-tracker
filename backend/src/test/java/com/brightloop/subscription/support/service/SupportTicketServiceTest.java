package com.brightloop.subscription.support.service;

import com.brightloop.subscription.auth.entity.Customer;
import com.brightloop.subscription.auth.entity.User;
import com.brightloop.subscription.auth.repository.CustomerRepository;
import com.brightloop.subscription.auth.repository.UserRepository;
import com.brightloop.subscription.chatbot.entity.ChatMessage;
import com.brightloop.subscription.chatbot.entity.ChatSession;
import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import com.brightloop.subscription.chatbot.entity.MessageSender;
import com.brightloop.subscription.chatbot.repository.ChatMessageRepository;
import com.brightloop.subscription.chatbot.repository.ChatSessionRepository;
import com.brightloop.subscription.support.dto.AddCommentRequest;
import com.brightloop.subscription.support.dto.CommentResponse;
import com.brightloop.subscription.support.dto.TicketDetailResponse;
import com.brightloop.subscription.support.dto.UpdateTicketStatusRequest;
import com.brightloop.subscription.support.entity.SupportTicket;
import com.brightloop.subscription.support.entity.TicketComment;
import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;
import com.brightloop.subscription.support.repository.SupportTicketRepository;
import com.brightloop.subscription.support.repository.TicketCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @Mock
    private TicketCommentRepository commentRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    private SupportTicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new SupportTicketService(
                ticketRepository,
                commentRepository,
                chatSessionRepository,
                chatMessageRepository,
                customerRepository,
                userRepository
        );
    }

    @Test
    @DisplayName("Escalate chat session creates ticket with OPEN status and MEDIUM priority")
    void testEscalateChatSessionCreatesTicket() {
        ChatSession session = ChatSession.builder()
                .id(10L)
                .customerId(55L)
                .userId(1L)
                .status(ChatSessionStatus.BOT)
                .build();

        when(chatSessionRepository.findByIdAndCustomerId(10L, 55L)).thenReturn(Optional.of(session));

        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket t = invocation.getArgument(0);
            t.setId(101L);
            return t;
        });

        SupportTicket ticket = ticketService.escalateChatSession(10L, 55L, "Urgent billing inquiry");

        assertNotNull(ticket);
        assertEquals(TicketStatus.OPEN.name(), ticket.getStatus());
        assertEquals(TicketPriority.MEDIUM.name(), ticket.getPriority());
        assertEquals(55L, ticket.getCustomerId());
        assertEquals(10L, ticket.getSessionId());
        assertTrue(ticket.getNumber().startsWith("TKT-"));

        verify(chatSessionRepository).save(argThat(s ->
                s.getStatus() == ChatSessionStatus.ESCALATED && Long.valueOf(101L).equals(s.getTicketId())));
    }

    @Test
    @DisplayName("Get ticket detail includes chronological chat transcript")
    void testGetTicketDetailIncludesTranscript() {
        SupportTicket ticket = SupportTicket.builder()
                .id(200L)
                .number("TKT-000200")
                .customerId(55L)
                .sessionId(10L)
                .status(TicketStatus.OPEN)
                .priority(TicketPriority.MEDIUM)
                .subject("Test ticket")
                .createdAt(Instant.now())
                .build();

        when(ticketRepository.findById(200L)).thenReturn(Optional.of(ticket));

        Customer customer = Customer.builder()
                .id(55L)
                .companyName("Acme Corp")
                .contactName("Alice Smith")
                .build();
        when(customerRepository.findById(55L)).thenReturn(Optional.of(customer));

        ChatMessage m1 = ChatMessage.builder()
                .id(1L)
                .sessionId(10L)
                .sender(MessageSender.CUSTOMER)
                .body("Hello bot")
                .intent("UNKNOWN")
                .createdAt(Instant.now().minusSeconds(60))
                .build();

        ChatMessage m2 = ChatMessage.builder()
                .id(2L)
                .sessionId(10L)
                .sender(MessageSender.BOT)
                .body("Hello customer")
                .intent("UNKNOWN")
                .createdAt(Instant.now().minusSeconds(50))
                .build();

        when(chatMessageRepository.findAllBySessionIdOrderByCreatedAtAsc(10L)).thenReturn(List.of(m1, m2));
        when(commentRepository.findAllByTicketIdOrderByCreatedAtAsc(200L)).thenReturn(List.of());

        TicketDetailResponse detail = ticketService.getTicketDetail(200L);

        assertNotNull(detail);
        assertEquals("TKT-000200", detail.getNumber());
        assertEquals("Acme Corp", detail.getCompanyName());
        assertEquals(2, detail.getChatTranscript().size());
        assertEquals("CUSTOMER", detail.getChatTranscript().get(0).getSender());
        assertEquals("Hello bot", detail.getChatTranscript().get(0).getBody());
    }

    @Test
    @DisplayName("Staff can add comment to ticket")
    void testStaffCanAddComment() {
        SupportTicket ticket = SupportTicket.builder()
                .id(200L)
                .number("TKT-000200")
                .status(TicketStatus.OPEN)
                .build();
        when(ticketRepository.findById(200L)).thenReturn(Optional.of(ticket));

        User staff = User.builder().id(99L).fullName("Staff Jane").build();
        when(userRepository.findById(99L)).thenReturn(Optional.of(staff));

        when(commentRepository.save(any(TicketComment.class))).thenAnswer(invocation -> {
            TicketComment c = invocation.getArgument(0);
            c.setId(501L);
            return c;
        });

        AddCommentRequest request = AddCommentRequest.builder()
                .body("Customer contacted via phone")
                .internal(true)
                .build();

        CommentResponse commentResponse = ticketService.addComment(200L, 99L, request);

        assertNotNull(commentResponse);
        assertEquals(501L, commentResponse.getId());
        assertEquals("Customer contacted via phone", commentResponse.getBody());
        assertTrue(commentResponse.isInternal());
        assertEquals("Staff Jane", commentResponse.getAuthorName());
    }
}
