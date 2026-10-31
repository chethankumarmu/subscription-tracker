package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByNumber(String number);
    List<Invoice> findByCustomerId(Long customerId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findAllByOrderByIssueDateDesc();

    @Query("SELECT COALESCE(SUM(i.totalPaise), 0) FROM Invoice i")
    Long sumTotalInvoices();

    @Query("SELECT COALESCE(SUM(i.amountPaidPaise), 0) FROM Invoice i")
    Long sumAmountPaid();

    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

