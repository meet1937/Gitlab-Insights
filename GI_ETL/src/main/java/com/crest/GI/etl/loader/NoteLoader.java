package com.md.GI.etl.loader;

import com.md.GI.etl.model.Note;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NoteLoader {
  static final String query =
      ".notes"
          + "(nid, integration_id, id, project_id, author_id,"
          + "created_at, target_id, target_type, record_created_at, body, system,record_updated_at)"
          + "VALUES(:nid, :integrationId, :id, :projectId, :authorId,:createdAt,:targetId,:targetType,:recordCreatedAt,:body,:system,:recordUpdatedAt)"
          + "on conflict (integration_id,project_id,id)"
          + "update " +
              "set target_id=:targetId,target_type=:targetType,body=:body,record_updated_at=:recordUpdatedAt;";
  private final String addSchemaName = "insert into ";
  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public void insert(
      List<Note> noteList, UUID integrationId, Map<Long, UUID> projectIds, String schemaName) {
    List<Map<String, Object>> batchOfInputs = new ArrayList<>();
    for(int idx = 0; idx < noteList.size(); idx++) {
        Note note=noteList.get(idx);
        Map<String, Object> map = new HashMap();
        map.put("nid", UUID.randomUUID());
        map.put("integrationId", integrationId);
        map.put("id", note.getId());
        map.put("projectId", projectIds.get(note.getProjectId()));
        map.put("authorId", note.getAuthorId());
        map.put("createdAt", Timestamp.valueOf(note.getCreatedAt()));
        map.put("targetId", note.getTargetIid());
        map.put("targetType", note.getTargetType());
        map.put("recordCreatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        map.put("body", note.getBody());
        map.put("system", note.getSystem());
        map.put("recordUpdatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        batchOfInputs.add(map);
    }
    Map<String, Object>[] inputs = new HashMap[batchOfInputs.size()];
    namedParameterJdbcTemplate.batchUpdate(addSchemaName + schemaName + query, batchOfInputs.toArray(inputs));
  }
}
