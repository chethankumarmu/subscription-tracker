package com.brightloop.subscription.support.dto;

import com.brightloop.subscription.support.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketStatusRequest {
    @NotNull(message = "Ticket status cannot be null")
    private TicketStatus status;
    private Long assignedStaffId;

    public UpdateTicketStatusRequest() {}
    public UpdateTicketStatusRequest(TicketStatus status, Long assignedStaffId) {
        this.status = status;
        this.assignedStaffId = assignedStaffId;
    }

    public static UpdateTicketStatusRequestBuilder builder() { return new UpdateTicketStatusRequestBuilder(); }
    public static class UpdateTicketStatusRequestBuilder {
        private TicketStatus status;
        private Long assignedStaffId;
        public UpdateTicketStatusRequestBuilder status(TicketStatus status) { this.status = status; return this; }
        public UpdateTicketStatusRequestBuilder assignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public UpdateTicketStatusRequest build() { return new UpdateTicketStatusRequest(status, assignedStaffId); }
    }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }
}
