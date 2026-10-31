package com.brightloop.subscription.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotDataPayload {
    private InvoiceFact invoice;
    private SubscriptionFact subscription;
    private FaqFact faq;
    private TicketFact ticket;
}

