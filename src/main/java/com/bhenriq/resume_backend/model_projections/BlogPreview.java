package com.bhenriq.resume_backend.model_projections;

import com.bhenriq.resume_backend.model.BlogPostTag;

import java.time.Instant;
import java.util.Set;

/**
 * A non-entity element used only to implicitly type a return for the custom query used for blog previews.
 * Includes a subset of the true blog post elements.
 */
public interface BlogPreview {
    Long getId();
    Set<BlogPostTag> getPostTags();
    String getBlogHeader();
    String getBlogTitle();
    Instant getCreatedOn();
}
