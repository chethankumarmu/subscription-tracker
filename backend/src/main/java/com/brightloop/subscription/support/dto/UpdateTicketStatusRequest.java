package com.brightloop.subscription.support.dto;

import com.brightloop.subscription.support.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketStatusRequest {
    @NotNull(message = "Status is required")
    private TicketStatus status;
    private Long assignedStaffId;
}

