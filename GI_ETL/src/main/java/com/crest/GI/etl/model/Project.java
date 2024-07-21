package com.crest.GI.etl.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Project {
  Long id;
  String name;
  String path;
  Long parentId;
  LocalDateTime createdAt;
  LocalDateTime recordCreatedAt;
  LocalDateTime recordUpdatedAt;
}
