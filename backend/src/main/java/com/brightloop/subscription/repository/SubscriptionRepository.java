package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByNumber(String number);
    List<Subscription> findByCustomerId(Long customerId);
    List<Subscription> findByStatus(String status);
    List<Subscription> findAllByOrderByCreatedAtDesc();
}

