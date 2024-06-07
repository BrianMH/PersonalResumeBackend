package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic DTO for our blog post. Sanitation is performed on the service layer, so this DTO can hold some potentially
 * damaging HTML content if not properly sanitized.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("headerFilename")
    private String blogHeader;
    @JsonProperty("title")
    private String blogTitle;
    @JsonProperty("content")
    private String blogContent;

    @JsonProperty("created")
    private Instant createdOn;
    @JsonProperty("updated")
    private Instant updatedOn;
    @JsonProperty("published")
    private Boolean published;

    @JsonProperty("postTags")
    private Set<BlogPostTagDTO> postTags;

    /**
     * This is a special property only present in the DTO. Because the filenames are preserved for the images, we can
     * allow the server to pre-emptively save values in the S3 bucket asynchronously.
     */
    @JsonProperty("imageMappings")
    private Map<String, String> imageUrls;

    @Override
    public String toString() {
        return String.format("{BlogPostDTO (id = %d) == [title = %s] + [content = %s] + [header = %s] + [tags = %s] }",
                id, blogTitle, blogContent.substring(0, 10), blogHeader, postTags);
    }
}
