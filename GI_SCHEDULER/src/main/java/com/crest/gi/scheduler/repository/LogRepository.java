package com.crest.gi.scheduler.repository;

import com.crest.gi.scheduler.enums.Status;
import com.crest.gi.scheduler.model.SchedulerTask;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for SchedulerLog */
@Repository
public interface LogRepository extends JpaRepository<SchedulerTask, Integer> {
  List<SchedulerTask> findByStatusAndRetryCountLessThanAndRetryCountGreaterThan(
      Status status, int retryCount, int minRetryCount);
}
