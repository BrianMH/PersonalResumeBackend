package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    @JsonProperty("id")
    private String id;

    private String type;
    private String provider;
    private String providerAccountId;
    private String accessToken;
    private String tokenExpiry;
}
