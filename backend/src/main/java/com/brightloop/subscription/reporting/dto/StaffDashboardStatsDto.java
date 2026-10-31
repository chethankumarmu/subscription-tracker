package com.brightloop.subscription.reporting.dto;

public class StaffDashboardStatsDto {
    private Long activeSubscriptionsCount;
    private Long totalInvoicesCount;
    private Long openTicketsCount;
    private Long pendingInvoicesCount;

    public StaffDashboardStatsDto() {}

    public StaffDashboardStatsDto(Long activeSubscriptionsCount, Long totalInvoicesCount, Long openTicketsCount, Long pendingInvoicesCount) {
        this.activeSubscriptionsCount = activeSubscriptionsCount;
        this.totalInvoicesCount = totalInvoicesCount;
        this.openTicketsCount = openTicketsCount;
        this.pendingInvoicesCount = pendingInvoicesCount;
    }

    public Long getActiveSubscriptionsCount() { return activeSubscriptionsCount; }
    public Long getTotalInvoicesCount() { return totalInvoicesCount; }
    public Long getOpenTicketsCount() { return openTicketsCount; }
    public Long getPendingInvoicesCount() { return pendingInvoicesCount; }
}

