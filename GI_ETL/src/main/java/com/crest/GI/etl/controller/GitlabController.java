package com.crest.GI.etl.controller;

import com.crest.GI.etl.fetcher.MergeRequestFetcher;
import com.crest.GI.etl.fetcher.NoteFetcher;
import com.crest.GI.etl.fetcher.UserFetcher;
import com.crest.GI.etl.fetcher.ProjectFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component("gitlabController")
public class GitlabController implements Controller {
    private final MergeRequestFetcher mergeRequestFetcher;
    private final NoteFetcher noteFetcher;
    private final ProjectFetcher projectFetcher;
    private final UserFetcher userFetcher;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GitlabController(MergeRequestFetcher mergeRequestFetcher , NoteFetcher noteFetcher , ProjectFetcher projectFetcher , UserFetcher userFetcher , JdbcTemplate jdbcTemplate) {
        this.mergeRequestFetcher = mergeRequestFetcher;
        this.noteFetcher = noteFetcher;
        this.projectFetcher = projectFetcher;
        this.userFetcher = userFetcher;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void ingestion(UUID schedulerId , UUID etlId , UUID integrationId , String path , String schemaName) {
        try {
            Map<Long, UUID> projectIds = projectFetcher.fetch(path + "projects/" , integrationId , schemaName);
            userFetcher.fetch(path + "users/" , integrationId , schemaName);
            mergeRequestFetcher.fetch(path + "mergeRequests/" , integrationId , schemaName , projectIds);
            noteFetcher.fetch(path + "notes/" , integrationId , schemaName, projectIds);
        } catch (Exception e) {
            log.error("Some error occurred while loading  into database");
            updateEtlStatus("FAILED" , etlId , schemaName);
            return;
        }
        updateEtlStatus("SUCCESS" , etlId , schemaName);
        updateSchedulerStatus(schedulerId , etlId);
        log.info("Notes Data added to the database");
    }

    public void updateEtlStatus(String status , UUID etlId , String schemaName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String timeString = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        String etlQuery = "update " + schemaName + ".etl_tasks set status='" + status + "',completed_at='" + timeString + "'where id ='" + etlId + "';";
        jdbcTemplate.execute(etlQuery);
        log.info("ETL status updated");
    }

    public void updateSchedulerStatus(UUID schedulerId , UUID etlId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String timeString = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        String updateQuery = "UPDATE scheduler.scheduler_task SET aggregated_at = '" + timeString + "' where id='" +
                schedulerId + "';";
        jdbcTemplate.execute(updateQuery);
        log.info("Scheduler status updated");
    }
}
