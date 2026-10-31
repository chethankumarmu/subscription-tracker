package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRuleRepository extends JpaRepository<TaxRule, Long> {
    List<TaxRule> findByIsActiveTrue();
}

