package com.brightloop.subscription.subscriptions.repository;

import com.brightloop.subscription.subscriptions.entity.SubscriptionLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionLineRepository extends JpaRepository<SubscriptionLine, Long> {
    List<SubscriptionLine> findBySubscriptionId(Long subscriptionId);
}

