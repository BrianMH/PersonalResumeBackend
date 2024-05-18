package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.*;
import com.bhenriq.resume_backend.exception.CreationException;
import com.bhenriq.resume_backend.exception.GetArgumentException;
import com.bhenriq.resume_backend.exception.InvalidUrlException;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.service.BlogService;
import com.bhenriq.resume_backend.service.S3BucketService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for our blog posts and blog tags. Note that blog tags are not quite required to create manually, as implicitly
 * passing a null ID for a blog tag DTO would imply the lack of the current tag within the database already.
 */
@RestController
@RequestMapping("/api/blog")
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private S3BucketService bucketService;

    @Value("${CDN_PATH}")
    private String CDN_PATH;

    /**
     * Creates a new blog post given a BlogPostDTO object that contains all the relevant fields for the blog. Note that
     * HTML input isn't sanitized as all of that will be handled in the front end.
     * @param postContent the DTO containing the blog post information
     * @return a CREATED response on successful creation.
     */
    @PostMapping("/posts/new")
    public ResponseEntity<BlogPostDTO> createNewPost(@RequestBody BlogPostDTO postContent) {
        // in order to make sure update operations don't occur here, make sure that content has no set id
        if(postContent.getId() != null)
            throw new CreationException("Post to create cannot have a pre-defined post ID");

        // and we make sure that all the image URLs are valid
        Map<String, String> improperStrings = postContent.getImageUrls()
                                                    .entrySet()
                                                    .stream()
                                                    .filter(keyValPair -> !bucketService.checkValidImageUrl(keyValPair.getValue()))
                                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                            (x, y) -> {throw new IllegalStateException("Duplicate keys found.");}, HashMap::new));
        if(!improperStrings.isEmpty())
            throw new InvalidUrlException("Invalid URLs present within post creation object.", improperStrings);

        // And then add our object
        BlogPostDTO retPost = blogService.addBlogPost(postContent);

        // Before we return the blog post, we can get started with the image uploads and async queue them.
        // We make sure that the upload path doesn't match our CDN as that would imply it already exists in the bucket.
        String bucketPath = String.format("dynamic/%d", retPost.getId());
        postContent.getImageUrls().forEach((key, value) -> {
            if(!value.startsWith(CDN_PATH))
                bucketService.addImageToBucketAsync(key, value, bucketPath);
        });

        return ResponseEntity.status(HttpServletResponse.SC_CREATED).body(retPost);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogPostDTO> getBlogPost(@PathVariable Long id) {
        BlogPostDTO foundPost = blogService.getBlogPostById(id);

        if(foundPost == null)
            throw new NotFoundException(String.format("Post with id %d does not exist in database.", id));
        else
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(foundPost);
    }

    @GetMapping("/posts/{id}/preview")
    public ResponseEntity<BlogPreviewDTO> getBlogPostPreview(@PathVariable Long id) {
        BlogPreviewDTO foundPost = blogService.getBlogPreviewById(id);

        if(foundPost == null)
            throw new NotFoundException(String.format("Post with id %d does not exist in database.", id));
        else
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(foundPost);
    }

    /**
     * Takes in a set of mappings between on-blog images and the true URLs as the values and returns pairs which
     * are invalid due to a problem with the source. In other words, if the return is an empty map, then all links
     * are considered valid.
     *
     * @param relUrlContainer the input mappings between s3 filenames and image sources
     * @return the entries which do not have valid image source
     */
    @PostMapping("/posts/validateUrls")
    public ResponseEntity<BlogUrlDTO> validateImageUrls(@RequestBody BlogUrlDTO relUrlContainer) {
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(new BlogUrlDTO(
                relUrlContainer
                        .getImageUrls()
                        .entrySet()
                        .stream()
                        .filter(keyValPair -> !bucketService.checkValidImageUrl(keyValPair.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (x, y) -> {throw new IllegalStateException("Duplicate keys found.");}, HashMap::new))
        ));
    }

    /**
     * Basic function to simply receive all tags which currently exist in the backend
     * @return the collection of tags that exist in the server
     */
    @GetMapping("/tags/all")
    public ResponseEntity<List<BlogPostTagDTO>> getAllTags() {
        return ResponseEntity.status(HttpServletResponse.SC_OK).body(blogService.getAllPostTags());
    }

    /**
     * Returns the number of pages associated with a given post tag based on the user's designated page size.
     * @param pageSize the number of posts the front-end will render
     * @param tagName the name of the uniquely identifying post tag
     * @return the number of pages that can be returned if querying /paged manually
     */
    @GetMapping("/posts/paged/{pageSize}")
    public ResponseEntity<Long> getNumberPagesWithQuery(@PathVariable int pageSize,
                                                        @RequestParam(required = false) String tagName) {
        if(pageSize < 0)
            throw new GetArgumentException(String.format("%d is not a valid page size.", pageSize));

        if(tagName != null && !tagName.isEmpty())
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(blogService.getNumPages(pageSize, tagName));
        else
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(blogService.getNumPages(pageSize));
    }

    @GetMapping("/posts/paged/{pageSize}/{page}")
    public ResponseEntity<List<IdWrapperDTO>> getPagedPostIdsWithQuery(@PathVariable int pageSize,
                                                                             @PathVariable int page,
                                                                             @RequestParam(required = false) String tagName) {
        if(pageSize < 0)
            throw new GetArgumentException(String.format("%d is not a valid page size.", pageSize));

        if(tagName != null && !tagName.isEmpty())
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(blogService.getOffsetPagedBlogIds(page, pageSize, tagName));
        else
            return ResponseEntity.status(HttpServletResponse.SC_OK).body(blogService.getOffsetPagedBlogIds(page, pageSize));
    }
}
