package com.crest.GI.etl.service;

import com.crest.GI.etl.controller.Controller;
import com.dropbox.core.DbxException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("etlDbService")
@Slf4j
public class DbService {
  private final JdbcTemplate jdbcTemplate;
  private final ApplicationContext context;

  @Autowired
  public DbService(JdbcTemplate jdbcTemplate, ApplicationContext context) {
    this.jdbcTemplate = jdbcTemplate;
    this.context = context;
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60, initialDelay = 1000)
  public void processSchedulerLogs() {
    String query =
        "select * from scheduler.scheduler_task where status = 'SUCCESS' and aggregated_at IS NULL;";
    List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
    if (queryResult.isEmpty()) {
      return;
    }
    ExecutorService executor = Executors.newFixedThreadPool(queryResult.size());
    for (Map<String, Object> row : queryResult) {
      UUID etlId = UUID.randomUUID();
      UUID schedulerId = (UUID) row.get("id");
      UUID integrationId = (UUID) row.get("integration_id");
      String integrationIdString = integrationId.toString();
      String schedulerIdString = schedulerId.toString();
      String schemaName = "crest";
      String path = (String) row.get("path");
      String integrationName = (String) row.get("integration_name");
      String integrationNameString = integrationName + "Controller";
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
      String startTimeString = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
      String sql =
          "insert into etl_tasks(id, scheduler_id, integration_id,"
              + " integration_name, status, started_at) values ('"
              + etlId
              + "', '"
              + schedulerIdString
              + "', '"
              + integrationIdString
              + "', '"
              + integrationName
              + "', 'RUNNING', '"
              + startTimeString
              + "');";
      jdbcTemplate.execute("set schema '" + schemaName + "'");
      jdbcTemplate.execute(sql);
      String failureQuery = "update etl_tasks set status='FAILED' where id = '" + etlId + "';";
      try {
        Controller controller = (Controller) context.getBean(integrationNameString);
        executor.submit(
            () -> {
              try {
                controller.ingestion(schedulerId, etlId, integrationId, path, schemaName);
              } catch (IOException e) {
                log.info("IOException occurred while reading data from dropbox");
                jdbcTemplate.execute(failureQuery);
              } catch (SQLException e) {
                log.info("SQLException occurred while reading data from dropbox");
                jdbcTemplate.execute(failureQuery);
              } catch (DbxException e) {
                throw new RuntimeException(e);
              }
            });
      } catch (NoSuchBeanDefinitionException e) {
        log.info(row.get("controller") + " Controller is not available for ETL Process");
        jdbcTemplate.execute(failureQuery);
      }
    }
  }
}
