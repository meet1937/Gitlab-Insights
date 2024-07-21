package com.crest.GI.etl.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
  Long id;
  String username;
  String name;
  String state;
  LocalDateTime recordCreatedAt;
  LocalDateTime recordUpdatedAt;
}
