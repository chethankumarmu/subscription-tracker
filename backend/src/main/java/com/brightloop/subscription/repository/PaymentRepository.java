package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);
    List<Payment> findAllByOrderByPaidOnDesc();

    @Query("SELECT COALESCE(SUM(p.amountPaise), 0) FROM Payment p")
    Long sumAllPayments();
}
