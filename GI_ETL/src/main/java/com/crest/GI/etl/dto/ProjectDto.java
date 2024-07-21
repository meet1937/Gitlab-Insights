package com.crest.GI.etl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ProjectDto {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("description")
  private String description;

  @JsonProperty("name")
  private String name;

  @JsonProperty("name_with_namespace")
  private String nameWithNamespace;

  @JsonProperty("path")
  private String path;

  @JsonProperty("path_with_namespace")
  private String pathWithNamespace;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("default_branch")
  private String defaultBranch;

  @JsonProperty("tag_list")
  private List<String> tagList;

  @JsonProperty("topics")
  private List<String> topics;
  @JsonProperty("readme_url")
  private String readmeUrl;

  @JsonProperty("ssh_url_to_repo")
  private String sshUrlToRepo;

  @JsonProperty("http_url_to_repo")
  private String httpUrlToRepo;

  @JsonProperty("web_url")
  private String webUrl;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("star_count")
  private Integer starCount;

  @JsonProperty("last_activity_at")
  private LocalDateTime lastActivityAt;

  @JsonProperty("namespace")
  private NamespaceDto namespace;

  @JsonProperty("forks_count")
  private Long forksCount;
}
