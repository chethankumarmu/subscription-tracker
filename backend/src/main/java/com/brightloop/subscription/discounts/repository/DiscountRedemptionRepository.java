package com.brightloop.subscription.discounts.repository;

import com.brightloop.subscription.discounts.entity.DiscountRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRedemptionRepository extends JpaRepository<DiscountRedemption, Long> {
    List<DiscountRedemption> findByInvoiceId(Long invoiceId);
    List<DiscountRedemption> findBySubscriptionId(Long subscriptionId);
    long countByDiscountId(Long discountId);
}
