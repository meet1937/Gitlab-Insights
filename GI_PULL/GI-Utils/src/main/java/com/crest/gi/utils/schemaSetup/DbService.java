package com.md.gi.utils.schemaSetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setupSchema(String schemaName) {
        String query = "create schema if not exists " + schemaName;
        jdbcTemplate.execute(query);
        createUserTables(schemaName);
        createProjectsTable(schemaName);
        createMergeRequestsTable(schemaName);
        createNotesTable(schemaName);
        createEtlTaskTable(schemaName);
        createIntegrationTable();
        createSchedulerTaskTable();
    }

    void createUserTables(String schemaName) {
        String sql = "CREATE TABLE if not exists "+schemaName+".users (\n" +
                "uid uuid NOT NULL,\n" +
                "integration_id uuid NULL,\n" +
                "id int8 NULL,\n" +
                "username varchar(255) NULL,\n" +
                "name varchar(255) NULL,\n" +
                "state varchar(255) NULL,\n" +
                "record_created_at timestamptz NULL,\n" +
                "record_updated_at timestamp NULL,\n" +
                "CONSTRAINT users_id_integration_id_key UNIQUE (id, integration_id),\n" +
                "CONSTRAINT users_pkey PRIMARY KEY (uid)\n" +
                ");";
        jdbcTemplate.execute(sql);
    }

    void createProjectsTable(String schemaName) {
        String sql = "CREATE TABLE if not exists "+schemaName+".projects (\n" +
                "pid uuid NOT NULL,\n" +
                "integration_id uuid NULL,\n" +
                "id int8 NULL,\n" +
                "name varchar(255) NULL,\n" +
                "path text NULL,\n" +
                "parent_id int8 NULL,\n" +
                "created_at timestamp NULL,\n" +
                "record_created_at timestamp NULL,\n" +
                "record_updated_at timestamp NULL,\n" +
                "CONSTRAINT projects_id_integration_id_key UNIQUE (id, integration_id),\n" +
                "CONSTRAINT projects_pkey PRIMARY KEY (pid)\n" +
                ");";
        jdbcTemplate.execute(sql);
    }

    void createMergeRequestsTable(String schemaName) {
        String sql ="CREATE TABLE if not exists "+schemaName+".merge_request (\n" +
                "mid uuid NOT NULL,\n" +
                "integration_id uuid NULL,\n" +
                "iid int8 NULL,\n" +
                "project_id uuid NULL,\n" +
                "title varchar(255) NULL,\n" +
                "state varchar(255) NULL,\n" +
                "author_id int8 NULL,\n" +
                "assignee_id int8 NULL,\n" +
                "assignees_id _int8 NULL,\n" +
                "reviewer_id _int8 NULL,\n" +
                "merge_user_id int8 NULL,\n" +
                "merged_at timestamp NULL,\n" +
                "prepared_at timestamp NULL,\n" +
                "closed_by_user_id int8 NULL,\n" +
                "closed_at timestamp NULL,\n" +
                "created_at timestamp NULL,\n" +
                "updated_at timestamp NULL,\n" +
                "source_branch varchar(255) NULL,\n" +
                "target_branch varchar(255) NULL,\n" +
                "source_project_id int8 NULL,\n" +
                "target_project_id int8 NULL,\n" +
                "record_created_at timestamp NULL,\n" +
                "record_updated_at timestamp NULL,\n" +
                "CONSTRAINT merge_request_iid_integration_id_project_id_key UNIQUE (iid, integration_id, project_id),\n" +
                "CONSTRAINT merge_request_pkey PRIMARY KEY (mid),\n" +
                "CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES "+schemaName+".projects(pid));";

        jdbcTemplate.execute(sql);
    }

    void createNotesTable(String schemaName) {
        String sql = "CREATE TABLE if not exists "+schemaName+".notes (\n" +
                "nid uuid NOT NULL,\n" +
                "integration_id uuid NULL,\n" +
                "id int8 NULL,\n" +
                "project_id uuid NULL,\n" +
                "author_id int8 NULL,\n" +
                "body varchar(255) NULL,\n" +
                "created_at timestamp NULL,\n" +
                "target_id int8 NULL,\n" +
                "target_type varchar(255) NULL,\n" +
                "system boolean NULL,\n" +
                "record_created_at timestamp NULL,\n" +
                "CONSTRAINT notes_integration_id_project_id_id_key UNIQUE (integration_id, project_id, id),\n" +
                "CONSTRAINT notes_pkey PRIMARY KEY (nid),\n" +
                "CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES "+schemaName+".projects(pid));";

        jdbcTemplate.execute(sql);
    }

    void createEtlTaskTable(String schemaName) {
        String sql = "CREATE TABLE if not exists "+schemaName+".etl_tasks (\n" +
                "id uuid NOT NULL,\n" +
                "scheduler_id uuid NULL,\n" +
                "integration_id uuid NULL,\n" +
                "integration_name varchar(255) NULL,\n" +
                "status varchar(255) NULL,\n" +
                "started_at timestamp NULL,\n" +
                "completed_at timestamp NULL,\n" +
                "CONSTRAINT etl_tasks_pkey PRIMARY KEY (id)\n" +
                ");";
        jdbcTemplate.execute(sql);
    }

    void createIntegrationTable() {
        String sql = "create schema if not exists scheduler";
        jdbcTemplate.execute(sql);
        String query = "CREATE TABLE if not exists scheduler.integration (\n" +
                "last_executed timestamp(6) NULL,\n" +
                "id uuid NOT NULL,\n" +
                "api_key varchar(255) NULL,\n" +
                "controller varchar(255) NULL,\n" +
                "schema_name varchar(255) NULL,\n" +
                "status varchar(255) NULL,\n" +
                "url varchar(255) NULL,\n" +
                "CONSTRAINT integration_pkey PRIMARY KEY (id)\n" +
                ");\n";
        jdbcTemplate.execute(query);
    }

    void createSchedulerTaskTable() {
        String sql = "CREATE TABLE if not exists scheduler.scheduler_task (\n" +
                "retry_count int4 NOT NULL,\n" +
                "aggregated_at timestamp NULL,\n" +
                "completed_at timestamp NULL,\n" +
                "started_from timestamp NULL,\n" +
                "integration_id uuid NULL,\n" +
                "id varchar(255) NOT NULL,\n" +
                "integration_name varchar(255) NULL,\n" +
                "message_dropbox varchar(255) NULL,\n" +
                "message_pull varchar(255) NULL,\n" +
                "path varchar(255) NULL,\n" +
                "schema_name varchar(255) NULL,\n" +
                "status varchar(255) NULL,\n" +
                "CONSTRAINT scheduler_task_pkey PRIMARY KEY (id)\n" +
                ");\n";
        jdbcTemplate.execute(sql);
    }
}
