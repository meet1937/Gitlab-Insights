package com.md.GI.etl.loader;

import com.md.GI.etl.model.Project;
import java.sql.Timestamp;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectLoader {
  static final String insertQuery =
      ".projects"
          + "(pid, integration_id, id, name, path, parent_id, created_at,"
          + "record_created_at, record_updated_at)"
          + "VALUES(:pid,:integrationId,:id,:name,:path,:parentId,:createdAt,:recordCreatedAt,:recordUpdatedAt)"
          + "on CONFLICT(id, integration_id)"
          + "do\n"
          + "UPDATE SET name =:name, path =:path, parent_id =:parentId, created_at =:createdAt,"
          + "record_updated_at = :recordUpdatedAt";
  static final String selectQuery =
      "select id,pid from crest.projects where integration_id =:integrationId;";
  private final String addSchemaName = "insert into ";
  Map<Long, UUID> projectIds = new HashMap<>();
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public Map<Long, UUID> insert(List<Project> projectList, UUID integrationId, String schemaName) {

    List<Map<String, Object>> batchOfInputs = new ArrayList<>();
    Map<String, Object>[] inputs = new HashMap[batchOfInputs.size()];
    for (int idx = 0; idx < projectList.size(); idx++) {
      Project project = projectList.get(idx);
      Map<String, Object> map = new HashMap();
      map.put("pid", UUID.randomUUID());
      map.put("integrationId", integrationId);
      map.put("id", project.getId());
      map.put("name", project.getName());
      map.put("path", project.getPath());
      map.put("parentId", project.getParentId());
      map.put("createdAt", Timestamp.valueOf(project.getCreatedAt()));
      map.put("recordCreatedAt", project.getRecordCreatedAt() == null ? null : Timestamp.valueOf(project.getRecordCreatedAt()));
      map.put("recordUpdatedAt", project.getRecordUpdatedAt() == null ? null : Timestamp.valueOf(project.getRecordUpdatedAt()));
      batchOfInputs.add(map);
    }
    namedParameterJdbcTemplate.batchUpdate(
        addSchemaName + schemaName + insertQuery, batchOfInputs.toArray(inputs));
    log.info("Project Data added to the database");
    Map<String, Object> data = new HashMap<>();
    data.put("schemaName", schemaName);
    data.put("integrationId", integrationId);
    List<Map<String, Object>> queryResult =
        namedParameterJdbcTemplate.queryForList(selectQuery, data);
    queryResult.forEach(row -> projectIds.put((Long) row.get("id"), (UUID) row.get("pid")));
    return projectIds;
  }
}
