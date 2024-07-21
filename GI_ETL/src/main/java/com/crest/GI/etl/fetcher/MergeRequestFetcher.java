package com.md.GI.etl.fetcher;

import com.md.GI.etl.controller.GitlabController;
import com.md.GI.etl.dto.MergeRequestDto;
import com.md.GI.etl.loader.MergeRequestLoader;
import com.md.GI.etl.mapper.GitlabMapper;
import com.md.GI.etl.model.MergeRequest;
import com.md.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MergeRequestFetcher {
  DropboxService dropboxService;
  MergeRequestLoader mergeRequestLoader;
  ObjectMapper objectMapper;

  GitlabMapper gitlabMapper;

  @Autowired
  public MergeRequestFetcher(
      DropboxService dropboxService,
      MergeRequestLoader mergeRequestLoader,
      ObjectMapper objectMapper,
      GitlabMapper gitlabMapper) {
    this.dropboxService = dropboxService;
    this.mergeRequestLoader = mergeRequestLoader;
    this.objectMapper = objectMapper;
    this.gitlabMapper=gitlabMapper;
  }

  public void fetch(
      String folderPath, UUID integrationId, String schemaName, Map<Long, UUID> projectIds)
      throws DbxException {
    int count = dropboxService.numberOfFiles(folderPath) + 1;
    try{
        for(int idx = 1; idx<count; idx++) {
            List<MergeRequestDto> list =
                    objectMapper.readValue(
                            dropboxService.readFromDropbox(folderPath + idx + ".json"),
                            new TypeReference<List<MergeRequestDto>>() {});
            List<MergeRequest> mergeRequestList = gitlabMapper.toMergeRequestDto(list);
            mergeRequestLoader.insertMergeRequest(mergeRequestList, integrationId, schemaName, projectIds);
        }
    } catch (IOException e) {
        throw new RuntimeException(e);
    } catch (DbxException e) {
        throw new RuntimeException(e);
    }
  }
}
