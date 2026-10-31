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
public class InvoiceFact {
    private String number;
    private String status;
    private Long totalPaise;
    private String totalFormatted;
    private Long amountPaidPaise;
    private Long remainingPaise;
    private LocalDate dueDate;
    private LocalDate issueDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String currency;
}

