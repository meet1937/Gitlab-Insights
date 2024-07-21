package com.md.GI.etl.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Note {
  Long id;
  Long projectId;
  Long targetIid;
  String targetType;
  Long authorId;
  LocalDateTime createdAt;
  Boolean system;
  String body;
  LocalDateTime recordCreatedAt;
  LocalDateTime recordUpdatedAt;
}
