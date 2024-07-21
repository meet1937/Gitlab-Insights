package com.crest.gi.pull.controller;

import static org.mockito.Mockito.*;

import com.crest.gi.pull.datasource.MergeRequestDataSource;
import com.crest.gi.pull.datasource.NoteDataSource;
import com.crest.gi.pull.datasource.ProjectDataSource;
import com.crest.gi.pull.datasource.UserDataSource;
import com.crest.gi.pull.model.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GitlabControllerTest {
  @Mock MergeRequestDataSource mergeRequestDataSource;
  @Mock ProjectDataSource projectDataSource;
  @Mock UserDataSource userDataSource;
  @Mock NoteDataSource noteDataSource;
  @InjectMocks GitlabController gitlabController;

  @Test
   void testIngestion()  {
    MergeRequest mergeRequest = new MergeRequest();
    Project project = new Project();
    User user = new User();
    Note note = new Note();
    Stream<MergeRequest> mergeRequestStream = Stream.of(mergeRequest);
    Stream<Project> projectStream = Stream.of(project);
    Stream<User> userStream = Stream.of(user);
    Stream<Note> noteStream = Stream.of(note);
    when(mergeRequestDataSource.fetch(any(), any(), anyString(), anyString()))
        .thenReturn(mergeRequestStream);
    when(projectDataSource.fetch(any(), any(), anyString(), anyString())).thenReturn(projectStream);
    when(userDataSource.fetch(anyString(), anyString())).thenReturn(userStream);
    when(noteDataSource.fetch(anyString(), anyString())).thenReturn(noteStream);
    List<Data> expected =
        List.of(
            new Data("notes", noteStream),
            new Data("users", userStream),
            new Data("projects", projectStream),
            new Data("mergeRequests", mergeRequestStream));
    List<Data> result =
        gitlabController.ingestion(
            LocalDateTime.of(2024, Month.APRIL, 1, 11, 30, 53),
            LocalDateTime.of(2024, Month.APRIL, 1, 11, 30, 53),
            "baseUrl",
            "gitLabAccessKey");
    Assert.assertEquals(expected.size(), result.size());
  }
}
