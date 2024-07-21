package com.crest.GI.etl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NamespaceDto {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("path")
  private String path;

  @JsonProperty("kind")
  private String kind;

  @JsonProperty("full_path")
  private String fullPath;

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("web_url")
  private String webUrl;
}
