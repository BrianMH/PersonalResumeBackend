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
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ResumeEducationDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("degreeLocation")
    private String location;
    @JsonProperty("degreeType")
    private String degreeType;
    @JsonProperty("degreeFocus")
    private String degreeTitle;

    @JsonProperty("description")
    private EducationDescription description;

    @JsonProperty("degreeStart")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate started;
    @JsonProperty("degreeEnd")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ended;

    // empty ctor that pre-defines our description object and leaves everything else as null
    public ResumeEducationDTO() {
        this.description = new EducationDescription();
    }

    // and then expose the getters/setters for our hidden sub-class manually
    @JsonIgnore
    public Double getGpa() {
        return this.description.getGpa();
    }

    public void setGpa(Double gpa) {
        this.description.setGpa(gpa);
    }

    @JsonIgnore
    public String getFocus() {
        return this.description.getFocus();
    }

    public void setFocus(String focus) {
        this.description.setFocus(focus);
    }

    @JsonIgnore
    public Set<String> getTopics() {
        return this.description.getTopics();
    }

    public void setTopics(Set<String> topics) {
        this.description.setTopics(topics);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class EducationDescription {
    @JsonProperty("gpa")
    private Double gpa;
    @JsonProperty("focus")
    private String focus;
    @JsonProperty("topics")
    private Set<String> topics;
}