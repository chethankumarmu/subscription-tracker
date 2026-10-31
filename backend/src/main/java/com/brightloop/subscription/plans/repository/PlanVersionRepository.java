package com.brightloop.subscription.plans.repository;

import com.brightloop.subscription.plans.entity.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanVersionRepository extends JpaRepository<PlanVersion, Long> {
}

