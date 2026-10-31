package com.brightloop.subscription.jobs.dto;

import java.time.LocalDate;

public class BillingRunResponseDto {
    private int createdCount;
    private int skippedCount;
    private int failedCount;
    private LocalDate runForDate;
    private String status;

    public BillingRunResponseDto() {}

    public BillingRunResponseDto(int createdCount, int skippedCount, int failedCount, LocalDate runForDate, String status) {
        this.createdCount = createdCount;
        this.skippedCount = skippedCount;
        this.failedCount = failedCount;
        this.runForDate = runForDate;
        this.status = status;
    }

    public int getCreatedCount() { return createdCount; }
    public int getSkippedCount() { return skippedCount; }
    public int getFailedCount() { return failedCount; }
    public LocalDate getRunForDate() { return runForDate; }
    public String getStatus() { return status; }
}

