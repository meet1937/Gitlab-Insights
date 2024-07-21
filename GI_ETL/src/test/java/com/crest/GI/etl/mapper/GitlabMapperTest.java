package com.md.GI.etl.mapper;

import com.md.GI.etl.dto.MergeRequestDto;
import com.md.GI.etl.dto.NoteDto;
import com.md.GI.etl.dto.ProjectDto;
import com.md.GI.etl.dto.UserDto;
import com.md.GI.etl.model.MergeRequest;
import com.md.GI.etl.model.Note;
import com.md.GI.etl.model.Project;
import com.md.GI.etl.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GitlabMapperTest {
  GitlabMapper gitlabMapper = new GitlabMapper();
  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testToNote() throws IOException {
    NoteDto noteDto =
        objectMapper
            .findAndRegisterModules()
            .readValue(new File("src/test/resources/NoteDto.json"), NoteDto.class);
    Note expected =
        Note.builder()
            .id(35950L)
            .projectId(90L)
            .targetIid(7L)
            .targetType("DiffNote")
            .system(false)
            .body("that helper function will help here")
            .authorId(436L)
            .createdAt(LocalDateTime.parse("2024-03-13T11:33:53.874"))
            .build();
    List<Note> result = gitlabMapper.toNote(List.of(noteDto));
    Assertions.assertEquals(expected, result.get(0));
  }

  @Test
  void testToMergeRequestDto() throws IOException {
    MergeRequest expected =
        MergeRequest.builder()
            .id(11348L)
            .mergedAt(null)
            .iid(17L)
            .mergeUserId(null)
            .createdAt(LocalDateTime.parse("2024-04-01T08:07:28.068"))
            .authorId(282L)
            .preparedAt(LocalDateTime.parse("2024-04-01T08:07:34.144"))
            .projectId(90L)
            .state("opened")
            .title(
                "INGL-45 : Added a modal that allows the end user to change the layout of widgets on the dashboard")
            .closedAt(null)
            .targetBranch("dev_mayank")
            .sourceBranch("INGL-45")
            .recordCreatedAt(LocalDateTime.now())
            .recordUpdatedAt(LocalDateTime.now())
            .userNotesCount(0)
            .assigneeId(282L)
            .reviewerId(new Long[] {436L})
            .sourceProjectId(90L)
            .targetProjectId(90L)
            .assigneesIds(new Long[] {282L})
            .updatedAt(LocalDateTime.parse("2024-04-01T08:07:34.148"))
            .build();
    MergeRequestDto mergeRequestDto =
        objectMapper
            .findAndRegisterModules()
            .readValue(new File("src/test/resources/MergeRequestDto.json"), MergeRequestDto.class);
    List<MergeRequest> result = gitlabMapper.toMergeRequestDto(List.of(mergeRequestDto));
    expected.setRecordCreatedAt(result.get(0).getRecordCreatedAt());
    expected.setRecordUpdatedAt(result.get(0).getRecordUpdatedAt());
    Assertions.assertEquals(expected, result.get(0));
  }

  @Test
  void testToProject() throws IOException {
    ProjectDto projectDto =
        objectMapper
            .findAndRegisterModules()
            .readValue(new File("src/test/resources/ProjectDto.json"), ProjectDto.class);
    Project project =
        Project.builder()
            .id(90L)
            .name("gi-ui")
            .path(
                "internstraining2024/sbu_breakouttrainings_projects/int2024-gitlab-insights/gi-ui")
            .parentId(568L)
            .createdAt(LocalDateTime.parse("2024-02-23T09:42:28.648"))
            .build();
    List<Project> result = gitlabMapper.toProject(List.of(projectDto));
    Assertions.assertEquals(project, result.get(0));
  }

  @Test
  void testToUser() throws IOException {
    User user =
        User.builder()
            .id(332L)
            .username("aman.garg")
            .name("Aman Garg")
            .state("active")
            .recordCreatedAt(LocalDateTime.now())
            .recordUpdatedAt(LocalDateTime.now())
            .build();
    UserDto userDto =
        objectMapper
            .findAndRegisterModules()
            .readValue(new File("src/test/resources/UserDto.json"), UserDto.class);
    List<User> result = gitlabMapper.toUser(List.of(userDto));
    user.setRecordCreatedAt(result.get(0).getRecordCreatedAt());
    user.setRecordUpdatedAt(result.get(0).getRecordUpdatedAt());
    Assertions.assertEquals(user, result.get(0));
  }
}
