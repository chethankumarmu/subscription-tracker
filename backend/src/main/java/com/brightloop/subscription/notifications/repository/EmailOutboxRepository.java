package com.brightloop.subscription.notifications.repository;

import com.brightloop.subscription.notifications.entity.EmailOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    @Query("SELECT e FROM EmailOutbox e WHERE e.status = :status AND e.nextAttemptAt <= :now ORDER BY e.nextAttemptAt ASC")
    List<EmailOutbox> findPendingEmails(@Param("status") String status, @Param("now") OffsetDateTime now, Pageable pageable);

    List<EmailOutbox> findByStatusOrderByCreatedAtDesc(String status);
}
