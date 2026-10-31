package com.brightloop.subscription.jobs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("created")
    public int getCreated() {
        return createdCount;
    }

    @JsonProperty("skipped")
    public int getSkipped() {
        return skippedCount;
    }

    @JsonProperty("failed")
    public int getFailed() {
        return failedCount;
    }

    @JsonProperty("createdCount")
    public int getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(int createdCount) {
        this.createdCount = createdCount;
    }

    @JsonProperty("skippedCount")
    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    @JsonProperty("failedCount")
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public LocalDate getRunForDate() {
        return runForDate;
    }

    public void setRunForDate(LocalDate runForDate) {
        this.runForDate = runForDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
