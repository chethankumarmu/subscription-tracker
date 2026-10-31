package com.brightloop.subscription.subscriptions.dto;

public class RenewalRequestResponseDto {
    private Long ticketId;
    private String ticketNumber;
    private Long subscriptionId;
    private String subscriptionNumber;
    private String message;

    public RenewalRequestResponseDto() {}

    public RenewalRequestResponseDto(Long ticketId, String ticketNumber, Long subscriptionId, String subscriptionNumber, String message) {
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.subscriptionId = subscriptionId;
        this.subscriptionNumber = subscriptionNumber;
        this.message = message;
    }

    public Long getTicketId() { return ticketId; }
    public String getTicketNumber() { return ticketNumber; }
    public Long getSubscriptionId() { return subscriptionId; }
    public String getSubscriptionNumber() { return subscriptionNumber; }
    public String getMessage() { return message; }
}

