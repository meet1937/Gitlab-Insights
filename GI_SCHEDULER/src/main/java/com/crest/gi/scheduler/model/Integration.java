package com.crest.gi.scheduler.model;

import com.crest.gi.scheduler.enums.Status;
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

/** Model to save in database */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "integration", schema = "scheduler")
public class Integration {
  @Id UUID id;
  String url;
  String apiKey;
  String controller;
  String schemaName;
  LocalDateTime lastExecuted;

  @JdbcTypeCode(Types.VARCHAR)
  Status status;
}
