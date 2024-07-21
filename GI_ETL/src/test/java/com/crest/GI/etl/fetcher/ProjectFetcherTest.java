package com.md.GI.etl.fetcher;

import com.md.GI.etl.dto.ProjectDto;
import com.md.GI.etl.loader.ProjectLoader;
import com.md.GI.etl.mapper.GitlabMapper;
import com.md.GI.etl.model.Project;
import com.md.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectFetcherTest {
    @InjectMocks
    ProjectFetcher projectFetcher;
    @Mock
    DropboxService dropboxService;
    @Mock
    ProjectLoader projectLoader;
    @Mock
    GitlabMapper gitlabMapper;
    @Mock
    ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {

    }

    @Test
    void fetch() throws DbxException, IOException {
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
        UUID integrationId=UUID.randomUUID();
        String folderPath="/integrationId";
        String schemaName="crest";
        when(dropboxService.numberOfFiles(anyString())).thenReturn(1);
        when(dropboxService.readFromDropbox(anyString())).thenReturn(List.of(new ProjectDto()).toString());
        when(gitlabMapper.toProject(any())).thenReturn(List.of(project));
        Map<Long,UUID> map=new HashMap<>();
        map.put(project.getId(),UUID.randomUUID());
        when(projectLoader.insert(any(), any(), anyString())).thenReturn(map);
        Map<Long,UUID> result=projectFetcher.fetch(folderPath,integrationId,schemaName);
        assertEquals(map.size(),result.size());
        assertEquals(map,result);
    }

  @Test
  void handleDbxException() throws DbxException, IOException {
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
        UUID integrationId=UUID.randomUUID();
        String folderPath="/integrationId";
        String schemaName="crest";
        when(dropboxService.numberOfFiles(anyString())).thenReturn(1);
        when(dropboxService.readFromDropbox(anyString())).thenThrow(DbxException.class);
        Map<Long,UUID> map=new HashMap<>();
        map.put(project.getId(),UUID.randomUUID());
        assertThrows(RuntimeException.class,()->projectFetcher.fetch(folderPath,integrationId,schemaName));
    }
    @Test
    void handleIOException() throws DbxException, IOException {
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
        UUID integrationId=UUID.randomUUID();
        String folderPath="/integrationId";
        String schemaName="crest";
        when(dropboxService.numberOfFiles(anyString())).thenReturn(1);
        when(dropboxService.readFromDropbox(anyString())).thenThrow(IOException.class);
        Map<Long,UUID> map=new HashMap<>();
        map.put(project.getId(),UUID.randomUUID());
        assertThrows(RuntimeException.class,()->projectFetcher.fetch(folderPath,integrationId,schemaName));
    }
}