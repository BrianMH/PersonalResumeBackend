package com.bhenriq.resume_backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used in order to update a token by identifying the old token and applying the changes to that particular
 * account associated with the relevant token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenUpdateDTO {
    @JsonProperty("id")
    String userId;

    @JsonProperty("old_access_token")
    String oldAccessToken;
    @JsonProperty("old_expires_at")
    Long oldExpiresAt;
    @JsonProperty("new_access_token")
    String newAccessToken;
    @JsonProperty("new_expires_at")
    Long newExpiresAt;
}
