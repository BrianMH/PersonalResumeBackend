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

@Getter
@Setter
@AllArgsConstructor
public class ResumeExperienceDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("jobType")
    private String type;
    @JsonProperty("jobTitle")
    private String title;
    @JsonProperty("jobLocation")
    private String location;

    @JsonProperty("jobTimeStart")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate started;
    @JsonProperty("jobTimeEnd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ended;

    @JsonProperty("description")
    private ExperienceDescription expDescription;

    // empty ctor with predefined description
    public ResumeExperienceDTO() {
        this.expDescription = new ExperienceDescription();
    }

    // provide getters/setters for the hidden values
    @JsonIgnore
    public List<String> getBullets() {
        return this.expDescription.getBullets();
    }

    public void setBullets(List<String> bullets) {
        this.expDescription.setBullets(bullets);
    }

    @JsonIgnore
    public List<ReferenceDTO> getReferences() {
        return this.expDescription.getReferences();
    }

    public void setReferences(List<ReferenceDTO> references) {
        this.expDescription.setReferences(references);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ExperienceDescription {
    @JsonProperty("bullets")
    private List<String> bullets;

    @JsonProperty("references")
    private List<ReferenceDTO> references;
}