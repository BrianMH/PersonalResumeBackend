package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Transfer object for the ResumeProject object.
 */
@Getter
@Setter
@AllArgsConstructor
public class ResumeProjectDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("shortDescription")
    private String shortDescription;
    @JsonProperty("projectRole")
    private String projectRole;
    @JsonProperty("projectType")
    private String projectType;

    @JsonProperty("projectStart")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate started;
    @JsonProperty("projectEnd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate ended;

    @JsonProperty("content")
    private ResumeProjectContent projContent;

    // empty ctor with predefined description
    public ResumeProjectDTO() {
        this.projContent = new ResumeProjectContent();
    }

    // provide getters/setters for the hidden values
    @JsonIgnore
    public List<String> getBullets() {
        return this.projContent.getBullets();
    }

    public void setBullets(List<String> bullets) {
        this.projContent.setBullets(bullets);
    }

    @JsonIgnore
    public List<ReferenceDTO> getReferences() {
        return this.projContent.getReferences();
    }

    public void setReferences(List<ReferenceDTO> references) {
        this.projContent.setReferences(references);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ResumeProjectContent {
    @JsonProperty("bullets")
    private List<String> bullets;

    @JsonProperty("references")
    private List<ReferenceDTO> references;
}