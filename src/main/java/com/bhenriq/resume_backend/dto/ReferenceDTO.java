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
public class ReferenceDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("href")
    private String refUrl;
    @JsonProperty("icon")
    private String iconType;
    @JsonProperty("description")
    private String description;
}
