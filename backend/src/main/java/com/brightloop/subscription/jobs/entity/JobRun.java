package com.brightloop.subscription.jobs.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "job_runs")
public class JobRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "run_for_date")
    private LocalDate runForDate;

    @Column(nullable = false)
    private String status = "RUNNING";

    @Column(name = "created_count", nullable = false)
    private Integer createdCount = 0;

    @Column(name = "skipped_count", nullable = false)
    private Integer skippedCount = 0;

    @Column(name = "failed_count", nullable = false)
    private Integer failedCount = 0;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "triggered_by")
    private Long triggeredBy;

    public JobRun() {
        this.startedAt = OffsetDateTime.now();
        this.status = "RUNNING";
        this.createdCount = 0;
        this.skippedCount = 0;
        this.failedCount = 0;
    }

    public JobRun(String jobType, LocalDate runForDate, Long triggeredBy) {
        this();
        this.jobType = jobType;
        this.runForDate = runForDate;
        this.triggeredBy = triggeredBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
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

    public Integer getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(Integer createdCount) {
        this.createdCount = createdCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(OffsetDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(Long triggeredBy) {
        this.triggeredBy = triggeredBy;
    }
}

