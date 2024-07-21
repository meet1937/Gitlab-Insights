package com.crest.gi.scheduler.service;

import com.crest.gi.pull.model.Data;
import com.crest.gi.pull.service.PullService;
import com.crest.gi.scheduler.enums.Status;
import com.crest.gi.scheduler.model.Integration;
import com.crest.gi.scheduler.model.SchedulerTask;
import com.crest.gi.scheduler.repository.LogRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** Class used to execute the pull service method periodically */
@Service
@Slf4j
public class ClassMethodInvokeService {

  DropBoxHelperService dropBoxHelperService;
  IntegrationService integrationService;
  PullService pullService;
  LogRepository logRepository;
  RetryIntegrationService retryIntegrationService;

  @Autowired
  public ClassMethodInvokeService(
      DropBoxHelperService dropBoxHelperService,
      IntegrationService integrationService,
      PullService pullService,
      LogRepository logRepository,
      RetryIntegrationService retryIntegrationService) {
    this.dropBoxHelperService = dropBoxHelperService;
    this.integrationService = integrationService;
    this.pullService = pullService;
    this.logRepository = logRepository;
    this.retryIntegrationService = retryIntegrationService;
  }

  /** this method will invoke in each 10 seconds delay */
  @Scheduled(fixedDelay = 1000 * 10) // 10 seconds  delay
  public void invokeMethod() {
    List<SchedulerTask> failedIntegration = retryIntegrationService.getFailedIntegrationLogs();
    if (failedIntegration != null && !failedIntegration.isEmpty()) {
      log.info("Retry Integration Started");
      LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
      failedIntegration.forEach(
          i -> {
            if (i != null) {
              gettingDataAndStoring(i.getIntegrationId(), timestamp, i);
            }
          });
    }
    Map<UUID, LocalDateTime> map = integrationService.checkForExecution();
    if (!map.isEmpty()) {
      log.info("Data Processing Started");
      LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);
      ExecutorService executor = Executors.newFixedThreadPool(map.size());
      map.keySet().forEach(i -> executor.submit(() -> gettingDataAndStoring(i, timestamp, null)));
    }
  }

  /**
   * This method will take data from pull service and store it into the file system
   *
   * @param id
   * @param timestamp
   */
  public void gettingDataAndStoring(UUID id, LocalDateTime timestamp, SchedulerTask schedulerLog) {
    Integration credential = integrationService.getIntegerationCredentialById(id);
    if (credential == null) {
      return;
    }
    SchedulerTask logger;
    if (schedulerLog != null) {
      logger = schedulerLog;
    } else {
      logger =
          SchedulerTask.builder()
              .id(UUID.randomUUID().toString())
              .messagePull("Currently running")
              .messageDropbox("")
              .startedFrom(LocalDateTime.now(ZoneOffset.UTC))
              .integrationId(id)
              .integrationName(null)
              .status(Status.RUNNING)
              .schemaName(credential.getSchemaName())
              .aggregatedAt(null)
              .completedAt(null)
              .path(null)
              .build();
      logRepository.save(logger);
    }
    if (logger.getIntegrationName() == null) {
      logger.setIntegrationName(credential.getController());
    }
    Integration updatedCredential =
        Integration.builder()
            .url(credential.getUrl())
            .apiKey(credential.getApiKey())
            .lastExecuted(timestamp)
            .status(credential.getStatus())
            .schemaName(credential.getSchemaName())
            .controller(credential.getController())
            .id(credential.getId())
            .build();

    updatedCredential.setLastExecuted(timestamp);
    integrationService.updateIntegerationCredential(updatedCredential);

    String path =
        "/"
            + id
            + "/"
            + timestamp.getYear()
            + "/"
            + timestamp.getMonth()
            + "/"
            + timestamp.getDayOfMonth()
            + "/"
            + logger.getId()
            + "/";
    List<Data> dataList = null;
    try {
      log.info("Data Pulling is starting");
      dataList =
          pullService.ingestion(
              credential.getController(),
              credential.getLastExecuted(),
              timestamp,
              credential.getUrl(),
              credential.getApiKey());
      if (dataList != null && !dataList.isEmpty()) {
        dataList.forEach(
            data ->
                dropBoxHelperService.writeToDropBox(path, data.getDataType(), data.getDataList()));
      }
      log.info("Data was pulled successfully");
      logger.setMessagePull("Pull Completed");
      logRepository.save(logger);
    } catch (Exception e) {
      log.info("Some error occured in pull service");
      logger.setCompletedAt(LocalDateTime.now(ZoneOffset.UTC));
      logger.setStatus(Status.FAILED);
      logger.setMessagePull("Error occured due pull service exception");
      if (logger.getRetryCount() == RetryIntegrationService.MIN_RETRY_COUNT) {
        retryIntegrationService.addFailedIntegrationLog(logger);
      } else if (logger.getRetryCount() > RetryIntegrationService.MIN_RETRY_COUNT
          && logger.getRetryCount() < RetryIntegrationService.MAX_RETRY_COUNT) {
        logger.setRetryCount(logger.getRetryCount() + 1);
        retryIntegrationService.updateFailedIntegration(logger);
      } else if (logger.getRetryCount() == RetryIntegrationService.MAX_RETRY_COUNT) {
        updatedCredential.setStatus(Status.CLOSED);
        retryIntegrationService.removeFailedIntegrationLog(logger.getId());
      }
      logRepository.save(logger);
      updatedCredential.setLastExecuted(credential.getLastExecuted());
      integrationService.updateIntegerationCredential(updatedCredential);
      return;
    }
    if (logger.getRetryCount() > RetryIntegrationService.MIN_RETRY_COUNT) {
      retryIntegrationService.removeFailedIntegrationLog(logger.getId());
    }
    log.info("Data Processing Completed");
    logger.setCompletedAt(LocalDateTime.now(ZoneOffset.UTC));
    logger.setRetryCount(logger.getRetryCount());
    logger.setStatus(Status.SUCCESS);
    logger.setPath(path);
    logger.setMessageDropbox("Data Saving Completed Successfully");
    logRepository.save(logger);
  }
}
