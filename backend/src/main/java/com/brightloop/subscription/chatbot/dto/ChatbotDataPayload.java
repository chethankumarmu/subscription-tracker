package com.brightloop.subscription.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotDataPayload {
    private InvoiceFact invoice;
    private SubscriptionFact subscription;
    private FaqFact faq;
    private TicketFact ticket;

    public ChatbotDataPayload() {}

    public ChatbotDataPayload(InvoiceFact invoice, SubscriptionFact subscription, FaqFact faq, TicketFact ticket) {
        this.invoice = invoice;
        this.subscription = subscription;
        this.faq = faq;
        this.ticket = ticket;
    }

    public static ChatbotDataPayloadBuilder builder() {
        return new ChatbotDataPayloadBuilder();
    }

    public static class ChatbotDataPayloadBuilder {
        private InvoiceFact invoice;
        private SubscriptionFact subscription;
        private FaqFact faq;
        private TicketFact ticket;

        public ChatbotDataPayloadBuilder invoice(InvoiceFact invoice) { this.invoice = invoice; return this; }
        public ChatbotDataPayloadBuilder subscription(SubscriptionFact subscription) { this.subscription = subscription; return this; }
        public ChatbotDataPayloadBuilder faq(FaqFact faq) { this.faq = faq; return this; }
        public ChatbotDataPayloadBuilder ticket(TicketFact ticket) { this.ticket = ticket; return this; }

        public ChatbotDataPayload build() {
            return new ChatbotDataPayload(invoice, subscription, faq, ticket);
        }
    }

    public InvoiceFact getInvoice() { return invoice; }
    public void setInvoice(InvoiceFact invoice) { this.invoice = invoice; }
    public SubscriptionFact getSubscription() { return subscription; }
    public void setSubscription(SubscriptionFact subscription) { this.subscription = subscription; }
    public FaqFact getFaq() { return faq; }
    public void setFaq(FaqFact faq) { this.faq = faq; }
    public TicketFact getTicket() { return ticket; }
    public void setTicket(TicketFact ticket) { this.ticket = ticket; }
}
