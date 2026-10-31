package com.brightloop.subscription.jobs.repository;

import com.brightloop.subscription.jobs.entity.JobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, Long> {
    List<JobRun> findByJobTypeOrderByStartedAtDesc(String jobType);
}

