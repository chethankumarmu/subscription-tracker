package com.brightloop.subscription.billing.repository;

import com.brightloop.subscription.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByNumber(String number);

    Optional<Invoice> findBySubscriptionIdAndPeriodStart(Long subscriptionId, LocalDate periodStart);

    boolean existsBySubscriptionIdAndPeriodStart(Long subscriptionId, LocalDate periodStart);

    List<Invoice> findBySubscriptionId(Long subscriptionId);

    Optional<Invoice> findFirstBySubscriptionIdOrderByPeriodStartDesc(Long subscriptionId);

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(String status);

    List<Invoice> findAllByOrderByIssueDateDesc();

    @Query("SELECT COALESCE(SUM(i.totalPaise), 0) FROM Invoice i")
    Long sumTotalInvoices();

    @Query("SELECT COALESCE(SUM(i.amountPaidPaise), 0) FROM Invoice i")
    Long sumAmountPaid();
}
