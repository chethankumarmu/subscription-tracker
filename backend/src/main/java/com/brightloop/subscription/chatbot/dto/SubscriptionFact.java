package com.brightloop.subscription.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionFact {
    private String number;
    private String status;
    private boolean paused;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private LocalDate nextBillingDate;
    private String planName;
    private String planCode;
    private Long pricePaise;
    private String priceFormatted;
    private String billingCycle;
    private boolean allowPause;
    private boolean allowRenewal;
}

