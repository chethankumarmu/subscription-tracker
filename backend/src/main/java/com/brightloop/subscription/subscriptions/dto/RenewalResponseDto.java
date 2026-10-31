package com.brightloop.subscription.subscriptions.dto;

import java.time.LocalDate;

public record RenewalResponseDto(
        Long invoiceId,
        String invoiceNumber,
        Long subscriptionId,
        String subscriptionNumber,
        String status,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDate dueDate,
        Long totalPaise,
        String totalFormatted,
        LocalDate newExpiryDate
) {
}
