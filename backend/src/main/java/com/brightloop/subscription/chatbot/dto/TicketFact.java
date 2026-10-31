package com.brightloop.subscription.chatbot.dto;

import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketFact {
    private Long id;
    private String number;
    private TicketStatus status;
    private TicketPriority priority;
    private String subject;
}

