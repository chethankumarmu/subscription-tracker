package com.brightloop.subscription.plans.repository;

import com.brightloop.subscription.plans.entity.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanVersionRepository extends JpaRepository<PlanVersion, Long> {

    Optional<PlanVersion> findByPlanIdAndVersionNo(Long planId, Integer versionNo);

    List<PlanVersion> findByPlanIdOrderByVersionNoAsc(Long planId);

    Optional<PlanVersion> findTopByPlanIdOrderByVersionNoDesc(Long planId);

    @Query("SELECT pv FROM PlanVersion pv WHERE pv.plan.id = :planId AND pv.effectiveTo IS NULL AND pv.status = 'PUBLISHED'")
    Optional<PlanVersion> findCurrentPublishedVersion(@Param("planId") Long planId);
}
