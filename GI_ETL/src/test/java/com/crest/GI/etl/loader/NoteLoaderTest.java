package com.md.GI.etl.loader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.md.GI.etl.model.Note;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class NoteLoaderTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @InjectMocks
  private NoteLoader noteLoader;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testInsert() {
    List<Note> noteList = new ArrayList<>();
    UUID integrationId = UUID.randomUUID();
    Map<Long, UUID> projectIds = new HashMap<>();
    String schemaName = "testSchema";
    Note note =
        Note.builder()
            .id(1L)
            .projectId(100L)
            .authorId(100L)
            .createdAt(LocalDateTime.now())
            .targetIid(200L)
            .targetType("MergeRequest")
            .body("fksdf")
            .system(false)
            .build();
    noteList.add(note);
    when(namedParameterJdbcTemplate.batchUpdate(any(String.class), any(Map[].class)))
        .thenReturn(new int[] {1});
    noteLoader.insert(noteList, integrationId, projectIds, schemaName);
  }
}
