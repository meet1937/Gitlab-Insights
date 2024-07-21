package com.md.GI.etl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDto {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("username")
  private String username;

  @JsonProperty("name")
  private String name;

  @JsonProperty("state")
  private String state;

  @JsonProperty("locked")
  private Boolean locked;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("web_url")
  private String webUrl;
}
