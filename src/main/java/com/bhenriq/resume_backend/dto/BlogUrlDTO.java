package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Only used to validate the URLs that will make up the images on the page.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogUrlDTO {
    @JsonProperty("imageMappings")
    private Map<String, String> imageUrls;
}
