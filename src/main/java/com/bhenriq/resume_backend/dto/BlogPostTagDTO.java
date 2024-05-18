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
public class BlogPostTagDTO {
    @JsonProperty("id")
    public Long id;

    @JsonProperty("tagName")
    public String tagName;

    @Override
    public String toString() {
        return String.format("{BlogPostTagDTO (id = %d) == [name = %s] }", id, tagName);
    }
}
