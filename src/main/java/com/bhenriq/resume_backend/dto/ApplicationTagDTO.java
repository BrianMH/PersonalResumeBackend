package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationTagDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("assignedTo")
    @JsonIgnore
    private List<ApplicationDTO> relatedApplications;

    @Override
    public String toString() {
        return String.format("{TagDTO (id=%s) - [name=%s] - [description=%s] - [relApps_size=%d]}",
                id, name, description, relatedApplications.size());
    }
}
