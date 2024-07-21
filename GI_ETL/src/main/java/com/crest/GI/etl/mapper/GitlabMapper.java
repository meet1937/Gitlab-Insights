package com.crest.GI.etl.mapper;

import com.crest.GI.etl.dto.MergeRequestDto;
import com.crest.GI.etl.dto.NoteDto;
import com.crest.GI.etl.dto.ProjectDto;
import com.crest.GI.etl.dto.UserDto;
import com.crest.GI.etl.model.MergeRequest;
import com.crest.GI.etl.model.Note;
import com.crest.GI.etl.model.Project;
import com.crest.GI.etl.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class GitlabMapper {
  public List<Note> toNote(List<NoteDto> noteDtoList) {
    List<Note> noteList = new ArrayList<>();
    noteDtoList.forEach(
        noteDto -> {
          noteList.add(
              Note.builder()
                  .id(noteDto.getId())
                  .projectId(noteDto.getProjectId())
                  .targetIid(noteDto.getNoteableIid())
                  .targetType(noteDto.getType())
                  .system(noteDto.getSystem())
                  .body(noteDto.getBody())
                  .authorId(noteDto.getAuthor().getId())
                  .createdAt(noteDto.getCreatedAt())
                  .build());
        });

    return noteList;
  }

  public List<MergeRequest> toMergeRequestDto(List<MergeRequestDto> mergeRequestDtos) {
    List<MergeRequest> mergeRequestList = new ArrayList<>();
    mergeRequestDtos.forEach(
        i -> {
          Long[] assigneesId = new Long[i.getAssignees().size()];
          Long[] reviewersId = new Long[i.getReviewers().size()];
          for (int j = 0; j < reviewersId.length; j++) {
            reviewersId[j] = i.getReviewers().get(j).getId();
          }
          for (int j = 0; j < assigneesId.length; j++) {
            assigneesId[j] = i.getAssignees().get(j).getId();
          }
          MergeRequest mergeRequest =
              MergeRequest.builder()
                  .mergedAt(i.getMergedAt() == null ? null : i.getMergedAt())
                  .id(i.getId())
                  .iid(i.getIid())
                  .mergeUserId(i.getMergeUser() == null ? null : i.getMergeUser().getId())
                  .createdAt(i.getCreatedAt())
                  .authorId(i.getAuthor().getId())
                  .preparedAt(i.getPreparedAt())
                  .state(i.getState())
                  .closedAt(i.getClosedAt())
                  .reviewerId(reviewersId)
                  .title(i.getTitle())
                  .sourceBranch(i.getSourceBranch())
                  .targetBranch(i.getTargetBranch())
                  .closedByUserId(i.getClosedBy() == null ? null : i.getClosedBy().getId())
                  .recordCreatedAt(LocalDateTime.now())
                  .recordUpdatedAt(LocalDateTime.now())
                  .userNotesCount(i.getUserNotesCount())
                  .sourceProjectId(i.getSourceProjectId())
                  .targetProjectId(i.getTargetProjectId())
                  .projectId(i.getProjectId())
                  .assigneeId(i.getAssignee() == null ? null : i.getAssignee().getId())
                  .assigneesIds(assigneesId)
                  .updatedAt(i.getUpdatedAt())
                  .build();
          mergeRequestList.add(mergeRequest);
        });
    return mergeRequestList;
  }

  public List<Project> toProject(List<ProjectDto> projectDtoList) {
    List<Project> projectList = new ArrayList<>();
    projectDtoList.forEach(
        projectDto -> {
          projectList.add(
              Project.builder()
                  .id(projectDto.getId())
                  .name(projectDto.getName())
                  .path(projectDto.getPathWithNamespace())
                  .parentId(projectDto.getNamespace().getParentId())
                  .createdAt(projectDto.getCreatedAt())
                  .build());
        });

    return projectList;
  }

  public List<User> toUser(List<UserDto> userList) {
    List<User> users = new ArrayList<>();
    userList.forEach(
        i -> {
          User user =
              User.builder()
                  .name(i.getName())
                  .username(i.getUsername())
                  .state(i.getState())
                  .recordCreatedAt(null)
                  .recordUpdatedAt(null)
                  .id(i.getId())
                  .build();
          users.add(user);
        });
    return users;
  }
}
