package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

/**
 * A cut down version of the BlogPostDTO that allows us to more conveniently wrap the previews of the posts without having
 * to pass the large block of blog content to the front-end for blog post previews.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogPreviewDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("headerFilename")
    private String blogHeader;
    @JsonProperty("title")
    private String blogTitle;

    @JsonProperty("created")
    private Instant createdOn;

    @JsonProperty("postTags")
    private Set<BlogPostTagDTO> postTags;
}
