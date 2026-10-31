package com.brightloop.subscription.repository;

import com.brightloop.subscription.model.PlanLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanLimitRepository extends JpaRepository<PlanLimit, Long> {
    List<PlanLimit> findByPlanVersionId(Long planVersionId);
}
