package com.crest.GI.etl.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MergeRequest {
  Long id;
  Long iid;
  String title;
  Long projectId;
  String state;
  Long authorId;
  Long assigneeId;
  Long[] assigneesIds;
  Long[] reviewerId;
  Long mergeUserId;
  LocalDateTime mergedAt;
  LocalDateTime preparedAt;
  Long closedByUserId;
  LocalDateTime closedAt;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  String sourceBranch;
  String targetBranch;
  Long sourceProjectId;
  Long targetProjectId;
  Integer userNotesCount;
  LocalDateTime recordCreatedAt;
  LocalDateTime recordUpdatedAt;
}
