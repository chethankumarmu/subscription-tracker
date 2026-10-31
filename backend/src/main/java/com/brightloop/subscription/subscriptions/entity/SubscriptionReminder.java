package com.brightloop.subscription.subscriptions.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subscription_reminders", uniqueConstraints = {
        @UniqueConstraint(name = "uq_subscription_reminders", columnNames = {"subscription_id", "days_before"})
})
public class SubscriptionReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "days_before", nullable = false)
    private Integer daysBefore;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private OffsetDateTime sentAt = OffsetDateTime.now();

    public SubscriptionReminder() {
    }

    public SubscriptionReminder(Long id, Long subscriptionId, Integer daysBefore, OffsetDateTime sentAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.daysBefore = daysBefore;
        this.sentAt = sentAt != null ? sentAt : OffsetDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = OffsetDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Integer getDaysBefore() {
        return daysBefore;
    }

    public void setDaysBefore(Integer daysBefore) {
        this.daysBefore = daysBefore;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long subscriptionId;
        private Integer daysBefore;
        private OffsetDateTime sentAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder subscriptionId(Long subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public Builder daysBefore(Integer daysBefore) {
            this.daysBefore = daysBefore;
            return this;
        }

        public Builder sentAt(OffsetDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public SubscriptionReminder build() {
            return new SubscriptionReminder(id, subscriptionId, daysBefore, sentAt);
        }
    }
}
