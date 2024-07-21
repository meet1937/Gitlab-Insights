package com.md.gi.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This dto will be used in refresh token method as a response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenDropBoxDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
}

