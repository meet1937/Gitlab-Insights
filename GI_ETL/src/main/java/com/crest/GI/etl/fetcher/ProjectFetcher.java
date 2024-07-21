package com.md.GI.etl.fetcher;

import com.md.GI.etl.dto.ProjectDto;
import com.md.GI.etl.loader.ProjectLoader;
import com.md.GI.etl.mapper.GitlabMapper;
import com.md.GI.etl.model.Project;
import com.md.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectFetcher {

  DropboxService dropboxService;
  ProjectLoader projectLoader;
  GitlabMapper mapper;
  ObjectMapper objectMapper;

  @Autowired
  public ProjectFetcher(
      DropboxService dropboxService,
      ProjectLoader projectLoader,
      GitlabMapper mapper,
      ObjectMapper objectMapper) {
    this.dropboxService = dropboxService;
    this.projectLoader = projectLoader;
    this.mapper = mapper;
    this.objectMapper = objectMapper;
  }

  public Map<Long, UUID> fetch(String folderPath, UUID integrationId, String schemaName)
      throws DbxException {
    Map<Long, UUID> projectIds = new HashMap<>();
    int count = dropboxService.numberOfFiles(folderPath) + 1;
    try {
      for (int idx = 1; idx < count; idx++) {
        List<ProjectDto> list =
            objectMapper.readValue(
                dropboxService.readFromDropbox(folderPath + idx + ".json"),
                new TypeReference<List<ProjectDto>>() {});
        List<Project> projects=mapper.toProject(list);
        projectIds.putAll(projectLoader.insert(projects,integrationId, schemaName));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (DbxException e) {
      throw new RuntimeException(e);
    }
    return projectIds;
  }
}
