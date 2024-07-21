package com.crest.gi.scheduler.service;

import com.crest.gi.scheduler.enums.Status;
import com.crest.gi.scheduler.model.SchedulerTask;
import com.crest.gi.scheduler.repository.LogRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This class helps to maintain the failed integration and do retry operations */
@Service
public class RetryIntegrationService {

  public static final int MAX_RETRY_COUNT = 5;
  public static final int MIN_RETRY_COUNT = 0;
  static List<SchedulerTask> failedIntegrationLogs = null;
  LogRepository schedulerLogRepository;

  @Autowired
  public RetryIntegrationService(LogRepository schedulerLogRepository) {
    this.schedulerLogRepository = schedulerLogRepository;
  }

  /**
   * Get current failed integration
   *
   * @return
   */
  public List<SchedulerTask> getFailedIntegrationLogs() {
    if (failedIntegrationLogs == null) {
      failedIntegrationLogs =
          schedulerLogRepository.findByStatusAndRetryCountLessThanAndRetryCountGreaterThan(
              Status.FAILED, MAX_RETRY_COUNT, MIN_RETRY_COUNT);
    }
    List<SchedulerTask> retryLogs = new ArrayList<>();
    if (failedIntegrationLogs != null && !failedIntegrationLogs.isEmpty()) {
      for (SchedulerTask schedulerLog : failedIntegrationLogs) {
        if (schedulerLog
            .getCompletedAt()
            .isBefore(
                LocalDateTime.now(ZoneOffset.UTC)
                    .minusMinutes(10L * schedulerLog.getRetryCount()))) {
          retryLogs.add(schedulerLog);
        }
      }
    }

    return retryLogs;
  }

  /**
   * updating the failed integration log
   *
   * @param log
   */
  public void updateFailedIntegration(SchedulerTask log) {
    for (int i = 0; i < failedIntegrationLogs.size(); i++) {
      if (failedIntegrationLogs.get(i).getId().equals(log.getId())) {
        failedIntegrationLogs.set(i, log);
      }
    }
  }

  /**
   * add to failed integration
   *
   * @param failedLog
   */
  public void addFailedIntegrationLog(SchedulerTask failedLog) {
    failedLog.setRetryCount(failedLog.getRetryCount() + 1);
    failedIntegrationLogs.add(failedLog);
  }

  /**
   * remove the failed integration if it sucessfully executed or execeeded the retry count
   *
   * @param logId
   */
  public void removeFailedIntegrationLog(String logId) {
    for (SchedulerTask schedulerLog : failedIntegrationLogs) {
      if (schedulerLog.getId().equals(logId)) {
        failedIntegrationLogs.remove(schedulerLog);
        return;
      }
    }
  }
}
