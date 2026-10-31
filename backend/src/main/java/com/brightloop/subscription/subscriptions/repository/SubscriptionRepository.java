package com.brightloop.subscription.subscriptions.repository;

import com.brightloop.subscription.subscriptions.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByNumber(String number);

    List<Subscription> findByCustomerId(Long customerId);

    List<Subscription> findByStatus(String status);

    List<Subscription> findAllByOrderByCreatedAtDesc();

    List<Subscription> findByStatusAndExpiryDateIn(String status, Collection<LocalDate> expiryDates);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND (s.expiryDate = :date7 OR s.expiryDate = :date1)")
    List<Subscription> findActiveExpiringOn(@Param("date7") LocalDate date7, @Param("date1") LocalDate date1);
}
