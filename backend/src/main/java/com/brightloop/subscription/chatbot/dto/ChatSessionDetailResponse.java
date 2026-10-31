package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.chatbot.entity.ChatSessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDetailResponse {
    private Long sessionId;
    private Long customerId;
    private ChatSessionStatus status;
    private Long ticketId;
    private String ticketNumber;
    private Instant startedAt;
    private Instant endedAt;
    private List<ChatMessageDto> messages;
}

