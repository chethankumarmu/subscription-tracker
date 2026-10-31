package com.brightloop.subscription.support.dto;

import com.brightloop.subscription.support.entity.TicketPriority;
import com.brightloop.subscription.support.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSummaryResponse {
    private Long id;
    private String number;
    private Long customerId;
    private String companyName;
    private String contactName;
    private Long sessionId;
    private String subject;
    private TicketStatus status;
    private TicketPriority priority;
    private Long assignedStaffId;
    private String assignedStaffName;
    private Instant createdAt;
    private Instant resolvedAt;
}

