package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanVersionRepository extends JpaRepository<PlanVersion, Long> {
    List<PlanVersion> findByPlanId(Long planId);
    Optional<PlanVersion> findByPlanIdAndVersionNo(Long planId, Integer versionNo);
}
