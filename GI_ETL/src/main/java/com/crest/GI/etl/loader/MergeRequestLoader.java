package com.crest.GI.etl.loader;

import com.crest.GI.etl.mapper.GitlabMapper;
import com.crest.GI.etl.model.MergeRequest;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MergeRequestLoader {
  static final String sql =
      ".merge_request\n"
          + "(mid,integration_id, iid, project_id, title, state, author_id, assignee_id,reviewer_id, merge_user_id, merged_at, prepared_at, closed_by_user_id, closed_at, created_at, updated_at, source_branch, target_branch, source_project_id, target_project_id,assignees_id,record_created_at,record_updated_at)\n"
          + "VALUES(:mid,:integrationId,:iid,:projectId,:title,:state,:authorId,:assigneeId,:reviewerId,:mergeUserId,:mergedAt,:preparedAt,:closedByUserId,:closedAt,:createdAt,:updatedAt,:sourceBranch,:targetBranch,:sourceProjectId,:targetProjectId,:assigneesId,:recordCreatedAt,:recordUpdatedAt)\n"
          + "on CONFLICT(project_id,integration_id,iid)\n"
          + "dO\n"
          + "UPDATE \n"
          + "SET title=:title, state=:state, assignee_id=:assigneeId, reviewer_id=:reviewerId, merge_user_id=:mergeUserId, merged_at=:mergedAt, prepared_at=:preparedAt, closed_by_user_id=:closedByUserId, closed_at=:closedAt, updated_at=:updatedAt, source_branch=:sourceBranch, target_branch=:targetBranch, source_project_id=:sourceProjectId, target_project_id=:targetProjectId,assignees_id=:assigneesId,record_updated_at=:recordUpdatedAt;";
  private final String addSchemaName = "insert into ";
  GitlabMapper mapper;
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public MergeRequestLoader(
      GitlabMapper mapper,
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.mapper = mapper;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public void insertMergeRequest(
      List<MergeRequest> mergeRequestList,
      UUID integrationId,
      String schemaName,
      Map<Long, UUID> projectList) {

    List<Map<String, Object>> batchOfInputs = new ArrayList<>();
    for (int idx = 0; idx < mergeRequestList.size(); idx++) {
      MergeRequest mergeRequest = mergeRequestList.get(idx);
      Map<String, Object> map = new HashMap();
      map.put("mid", UUID.randomUUID());
      map.put("integrationId", integrationId);
      map.put("iid", mergeRequest.getIid());
      map.put("projectId", projectList.get(mergeRequest.getProjectId()));
      map.put("title", mergeRequest.getTitle());
      map.put("state", mergeRequest.getState());
      map.put("authorId", mergeRequest.getAuthorId());
      map.put("assigneeId", mergeRequest.getAssigneeId());
      map.put("reviewerId", mergeRequest.getReviewerId());
      map.put("mergeUserId", mergeRequest.getMergeUserId());
      map.put("mergedAt", mergeRequest.getMergedAt() == null ? null : Timestamp.valueOf(mergeRequest.getMergedAt()));
      map.put("preparedAt", Timestamp.valueOf(mergeRequest.getPreparedAt()));
      map.put("closedByUserId", mergeRequest.getClosedByUserId());
      map.put("closedAt", mergeRequest.getClosedAt() == null ? null : Timestamp.valueOf(mergeRequest.getClosedAt()));
      map.put("createdAt", Timestamp.valueOf(mergeRequest.getCreatedAt()));
      map.put("updatedAt", Timestamp.valueOf(mergeRequest.getUpdatedAt()));
      map.put("sourceBranch", mergeRequest.getSourceBranch());
      map.put("targetBranch", mergeRequest.getTargetBranch());
      map.put("sourceProjectId", mergeRequest.getSourceProjectId());
      map.put("targetProjectId", mergeRequest.getTargetProjectId());
      map.put("assigneesId", mergeRequest.getAssigneesIds());
      map.put("recordCreatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
      map.put("recordUpdatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
      batchOfInputs.add(map);
    }
    Map<String, Object>[] inputs = new HashMap[batchOfInputs.size()];
    namedParameterJdbcTemplate.batchUpdate(
        addSchemaName + schemaName + sql, batchOfInputs.toArray(inputs));
    log.info("Merge Request Data Saved Successfully");
  }
}
