package com.brightloop.subscription.plans.repository;

import com.brightloop.subscription.plans.entity.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanVersionRepository extends JpaRepository<PlanVersion, Long> {
    Optional<PlanVersion> findByPlanIdAndVersionNo(Long planId, Integer versionNo);
}
