package com.brightloop.subscription.support.repository;

import com.brightloop.subscription.support.entity.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Optional<SupportTicket> findByNumber(String number);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    List<SupportTicket> findByStatus(String status);
    Page<SupportTicket> findAllByStatus(String status, Pageable pageable);
    List<SupportTicket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
