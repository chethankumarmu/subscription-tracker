package com.subscriptiontracker.subscription;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = "select nextval('subscription_number_seq')", nativeQuery = true)
    Long nextNumber();

    List<Subscription> findByCustomerIdOrderByIdDesc(Long customerId);

    /** Row lock so two billing runs can never process the same subscription at once. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Subscription s where s.id = :id")
    Optional<Subscription> findLockedById(@Param("id") Long id);

    /** Keyset pagination: always "id > lastId", never OFFSET. Returns ids only. */
    @Query("select s.id from Subscription s "
         + "where s.status = :status and s.paused = false "
         + "and s.nextBillingDate <= :target and s.id > :lastId order by s.id asc")
    List<Long> findDueIds(@Param("status") Subscription.Status status,
                          @Param("target") LocalDate target,
                          @Param("lastId") Long lastId,
                          Pageable pageable);
}
