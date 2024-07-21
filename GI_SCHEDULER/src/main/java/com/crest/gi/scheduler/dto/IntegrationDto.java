package com.crest.gi.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.*;

/** Dto for taking integration data from user */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationDto {
  @JsonProperty("id")
  UUID id;

  @Pattern(
      regexp =
          "^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$",
      message = "Invalid Url")
  @JsonProperty(value = "url")
  String url;

  @JsonProperty("api_key")
  String apiKey;

  @JsonProperty(value = "controller", required = false)
  String controller;

  @JsonProperty(value = "company_name", required = false)
  String schemaName;

  @Email(message = "Invalid Email Address")
  @JsonProperty(value = "email_id", required = false)
  String emailId;

  public void setUrl(String url) {
    if (url == null || url.length() < 3) {
      throw new IllegalArgumentException("Invalid Url");
    }
    this.url = url;
  }

  public void setApiKey(String apiKey) {
    if (apiKey == null || apiKey.length() < 3) {
      throw new IllegalArgumentException("Invalid Api Key");
    }
    this.apiKey = apiKey;
  }

  public void setController(String controller) {
    if (controller == null || controller.length() < 3) {
      throw new IllegalArgumentException("Invalid Controller ");
    }
    this.controller = controller;
  }
}
