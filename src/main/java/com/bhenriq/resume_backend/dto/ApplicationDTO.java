package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Represents a DTO for the application object that would be serialized
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ApplicationDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("jobTitle")
    private String jobTitle;

    @JsonProperty("company")
    private String companyName;

    @JsonProperty("appUrl")
    private String applicationUrl;

    @JsonProperty("tags")
    private List<ApplicationTagDTO> keywordStore;

    @JsonProperty("appliedAt")
    private Instant applyTime;

    @JsonProperty("respondedAt")
    private Instant responseTime;

    @Override
    public String toString() {
        return String.format("{AppDTO (id=%s) - [title=%s] - [company=%s] - [url=%s] - [tags=%s] - [apply=%s] - [response=%s]",
                id, jobTitle, companyName, applicationUrl, keywordStore, applyTime, responseTime);
    }
}
