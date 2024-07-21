package com.crest.GI.etl.fetcher;

import com.crest.GI.etl.dto.UserDto;
import com.crest.GI.etl.loader.UserLoader;
import com.crest.GI.etl.mapper.GitlabMapper;
import com.crest.GI.etl.model.User;
import com.crest.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class UserFetcher {
  DropboxService dropboxService;
  GitlabMapper gitlabMapper;
  UserLoader userLoader;
  ObjectMapper objectMapper;

  @Autowired
  public UserFetcher(
      DropboxService dropboxService,
      GitlabMapper gitlabMapper,
      UserLoader userLoader,
      ObjectMapper objectMapper) {
    this.dropboxService = dropboxService;
    this.gitlabMapper = gitlabMapper;
    this.userLoader = userLoader;
    this.objectMapper = objectMapper;
  }

  public void fetch(String folderPath, UUID integrationId, String schemaName) throws DbxException {
    int count = dropboxService.numberOfFiles(folderPath) + 1;
    try {
      for (int idx = 1; idx < count; idx++) {
        List<UserDto> list =
            objectMapper.readValue(
                dropboxService.readFromDropbox(folderPath + idx + ".json"),
                new TypeReference<List<UserDto>>() {});
        List<User> users=gitlabMapper.toUser(list);
        userLoader.insertUsers(users,integrationId, schemaName);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
