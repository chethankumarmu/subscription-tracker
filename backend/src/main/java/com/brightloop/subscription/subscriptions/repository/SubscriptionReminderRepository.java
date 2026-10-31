package com.brightloop.subscription.subscriptions.repository;

import com.brightloop.subscription.subscriptions.entity.SubscriptionReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionReminderRepository extends JpaRepository<SubscriptionReminder, Long> {

    boolean existsBySubscriptionIdAndDaysBefore(Long subscriptionId, Integer daysBefore);

    Optional<SubscriptionReminder> findBySubscriptionIdAndDaysBefore(Long subscriptionId, Integer daysBefore);
}
