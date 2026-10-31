package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Optional<SupportTicket> findByNumber(String number);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    List<SupportTicket> findByStatus(String status);
}
