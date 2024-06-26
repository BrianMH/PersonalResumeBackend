package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("token_type")
    private String type;
    @JsonProperty("provider")
    private String provider;
    @JsonProperty("providerAccountId")
    private String providerAccountId;

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_at")
    private Long tokenExpiry;

    // This has to be copied manually as we don't want to return the entire user object with it
    @JsonProperty("userId")
    private String userId;
}
