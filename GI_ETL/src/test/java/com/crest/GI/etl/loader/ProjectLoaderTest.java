package com.md.GI.etl.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.md.GI.etl.model.Project;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
public class ProjectLoaderTest {

  @Mock private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @InjectMocks private ProjectLoader projectLoader;

  @Test
  public void testInsertPositive() {
    List<Project> projectList = new ArrayList<>();
    UUID integrationId = UUID.randomUUID();
    String schemaName = "testSchema";
    Project project =
        Project.builder()
            .id(1L)
            .name("Test Project")
            .path("/path")
            .parentId(null)
            .createdAt(LocalDateTime.now())
            .recordCreatedAt(null)
            .recordUpdatedAt(null)
            .build();
    projectList.add(project);
    when(namedParameterJdbcTemplate.batchUpdate(any(String.class), any(Map[].class)))
        .thenReturn(new int[] {1});
    Map<String, Object> rowData = new HashMap<>();
    rowData.put("id", project.getId());
    rowData.put("pid", UUID.randomUUID());
    List<Map<String, Object>> queryResult = new ArrayList<>();
    queryResult.add(rowData);
    when(namedParameterJdbcTemplate.queryForList(any(String.class), any(Map.class)))
        .thenReturn(queryResult);
    Map<Long, UUID> result = projectLoader.insert(projectList, integrationId, schemaName);
    assertEquals(1, result.size());
    assertEquals(project.getId(), result.keySet().iterator().next());
  }
}
