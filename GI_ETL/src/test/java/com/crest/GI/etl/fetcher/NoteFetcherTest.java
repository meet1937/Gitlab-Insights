package com.md.GI.etl.fetcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.md.GI.etl.dto.NoteDto;
import com.md.GI.etl.loader.NoteLoader;
import com.md.GI.etl.mapper.GitlabMapper;
import com.md.GI.etl.model.Note;
import com.md.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteFetcherTest {
  @InjectMocks NoteFetcher noteFetcher;
  @Mock DropboxService dropboxService;
  @Mock
  ObjectMapper objectMapper;
  @Mock
  NoteLoader noteLoader;
  Note note;
  NoteDto noteDto;
  @Mock GitlabMapper gitlabMapper;


  @BeforeEach
  void setUp() {
    note =
        Note.builder()
            .id(10L)
            .body("hello")
            .projectId(1L)
            .targetIid(3L)
            .targetType("fsdf")
            .authorId(6L)
            .createdAt(LocalDateTime.now())
            .recordCreatedAt(LocalDateTime.now())
            .recordUpdatedAt(null)
            .system(false)
            .build();
    noteDto = new NoteDto();
  }

  @Test
  void handleDbxException() throws DbxException, IOException {
    String folderPath = "/files";
    UUID integrationId = UUID.randomUUID();
    String schemaName = "crest";
    Map<Long, UUID> projectIds = new HashMap<>();
    when(dropboxService.numberOfFiles(folderPath)).thenReturn(1);
    when(dropboxService.readFromDropbox(anyString())).thenThrow(DbxException.class);
    Assertions.assertThrows(RuntimeException.class,() -> noteFetcher.fetch(folderPath, integrationId, schemaName, projectIds));
  }
  @Test
  void handleIOException() throws DbxException, IOException {
    String folderPath = "/files";
    UUID integrationId = UUID.randomUUID();
    String schemaName = "crest";
    Map<Long, UUID> projectIds = new HashMap<>();
    when(dropboxService.numberOfFiles(folderPath)).thenReturn(1);
    when(dropboxService.readFromDropbox(anyString())).thenThrow(IOException.class);
    Assertions.assertThrows(RuntimeException.class,() -> noteFetcher.fetch(folderPath, integrationId, schemaName, projectIds));
  }
  @Test
  void fetch() throws DbxException, IOException {
    String folderPath = "/files";
    UUID integrationId = UUID.randomUUID();
    String schemaName = "crest";
    Map<Long, UUID> projectIds = new HashMap<>();
    when(dropboxService.numberOfFiles(folderPath)).thenReturn(1);
    when(dropboxService.readFromDropbox(anyString())).thenReturn("test");
    when(gitlabMapper.toNote(any())).thenReturn(List.of(note));
    noteFetcher.fetch(folderPath, integrationId, schemaName, projectIds);
  }

}
