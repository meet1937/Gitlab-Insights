package com.md.gi.scheduler.model;

import com.md.gi.scheduler.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

/** Model for scheduler log */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduler_task", schema = "scheduler")
public class SchedulerTask {
  @Id String id;
  LocalDateTime startedFrom;
  LocalDateTime completedAt;

  @JdbcTypeCode(Types.VARCHAR)
  Status status;

  String messagePull;
  String messageDropbox;
  UUID integrationId;
  String integrationName;
  String schemaName;
  LocalDateTime aggregatedAt;
  String path;
  int retryCount;
}
