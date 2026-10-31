package com.brightloop.subscription.plans.repository;

import com.brightloop.subscription.plans.entity.PlanLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanLimitRepository extends JpaRepository<PlanLimit, Long> {

    List<PlanLimit> findByPlanVersionId(Long planVersionId);

    Optional<PlanLimit> findByPlanVersionIdAndLimitKey(Long planVersionId, String limitKey);
}
