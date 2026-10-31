package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByCodeAndIsActiveTrue(String code);
    List<Discount> findByIsActiveTrue();
}
