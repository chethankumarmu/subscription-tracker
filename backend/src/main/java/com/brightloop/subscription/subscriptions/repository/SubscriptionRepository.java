package com.brightloop.subscription.subscriptions.repository;

import com.brightloop.subscription.subscriptions.entity.Subscription;
import com.brightloop.subscription.subscriptions.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Subscription> findFirstByCustomerIdAndStatusOrderByCreatedAtDesc(
            Long customerId, SubscriptionStatus status);

    Optional<Subscription> findFirstByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Subscription> findByNumberAndCustomerId(String number, Long customerId);
}

