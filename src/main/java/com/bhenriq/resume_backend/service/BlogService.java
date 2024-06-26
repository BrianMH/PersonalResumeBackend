package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.BlogPostDTO;
import com.bhenriq.resume_backend.dto.BlogPostTagDTO;
import com.bhenriq.resume_backend.dto.BlogPreviewDTO;
import com.bhenriq.resume_backend.dto.IdWrapperDTO;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.model.BlogPost;
import com.bhenriq.resume_backend.model.BlogPostTag;
import com.bhenriq.resume_backend.model_projections.IdWrapper;
import com.bhenriq.resume_backend.repository.BlogPostRepository;
import com.bhenriq.resume_backend.repository.BlogPostTagRepository;
import com.bhenriq.resume_backend.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles all relevant blog-related methods invoked by the controller. For now, we leave both of these here as the only
 * sole reason to create a tag would be for post-related reasons.
 */
@Service
@Slf4j
public class BlogService {
    @Autowired
    private BlogPostTagRepository blogTagRepo;

    @Autowired
    private BlogPostRepository blogPostRepo;

    @Autowired
    private ModelMapper converter;

    @Value("${CDN_PATH}")
    private String CDN_PATH;

    /**
     * Creates a new blog post given the passed DTO object
     * @param toAdd the object to convert to a real blog post
     * @return a new DTO object containing the same related fields but with a proper ID set
     */
    public BlogPreviewDTO addBlogPost(BlogPostDTO toAdd) {
        // first we convert back to the base object type
        BlogPost trueReflection = converter.map(toAdd, BlogPost.class);

        // Because we can have new blog tags, we make sure that all of them are added
        trueReflection.setPostTags(trueReflection.getPostTags().stream().map(blogPostTag -> {
            // for each blog tag, we either find the true element within the repository (due to uniqueness of tags),
            // or simply create it on the spot.
            if(blogPostTag.getId() != null)
                return blogPostTag; // assume value has been properly managed if it has a pre-set ID

            // otherwise attempt to find a known tag and create it otherwise
            String tagName = blogPostTag.getTagName();
            Optional<BlogPostTag> foundTag = blogTagRepo.findByTagNameIgnoreCase(tagName);
            return foundTag.orElseGet(() -> blogTagRepo.save(blogPostTag));
        }).collect(Collectors.toSet()));

        // and use our filename -> image mapping in the DTO in order to know what files will be added to the CDN
        trueReflection.setCdnImages(toAdd.getImageUrls().keySet());

        // and commit it to the repository with these proper adjustments
        return converter.map(blogPostRepo.save(trueReflection), BlogPreviewDTO.class);
    }

    public Pair<BlogPreviewDTO, List<String>> updateBlogPost(BlogPostDTO toUpdate) {
        // What we can do is simply manipulate the old object and replace all fields one-by-one since the update is
        // expected to be a full replacement.
        Optional<BlogPost> oldPostCont = blogPostRepo.findById(toUpdate.getId());
        BlogPost oldPost = oldPostCont.orElseThrow(() -> new NotFoundException("Post " + toUpdate.getId() + " does not exist."));

        // Because we can have new blog tags, we make sure that all of them are added
        oldPost.setPostTags(toUpdate.getPostTags().stream().map(blogPostTagDTO -> {
            // map it to the true object
            BlogPostTag blogPostTag = converter.map(blogPostTagDTO, BlogPostTag.class);

            // for each blog tag, we either find the true element within the repository (due to uniqueness of tags),
            // or simply create it on the spot.
            if(blogPostTag.getId() != null)
                return blogTagRepo.findById(blogPostTag.getId()).orElseThrow(); // assume value has been properly managed if it has a pre-set ID

            // otherwise attempt to find a known tag and create it otherwise
            String tagName = blogPostTag.getTagName();
            Optional<BlogPostTag> foundTag = blogTagRepo.findByTagNameIgnoreCase(tagName);
            return foundTag.orElseGet(() -> blogTagRepo.save(blogPostTag));
        }).collect(Collectors.toSet()));

        // overwrite the new content now
        oldPost.setBlogTitle(toUpdate.getBlogTitle());
        oldPost.setBlogContent(toUpdate.getBlogContent());
        oldPost.setBlogHeader(toUpdate.getBlogHeader());
        oldPost.setPublished(toUpdate.getPublished());

        // Now, for the different part, we can't just overwrite our set of CDN images. We must first identify which images
        // will require deletion and return those URLs for bucket management
        String bucketPath = String.format("dynamic/%d", toUpdate.getId());
        List<String> oldUrls = oldPost.getCdnImages().stream().map(keySuffix -> bucketPath + "/" + keySuffix).toList();
        Set<String> newUrls = new HashSet<>(toUpdate.getImageUrls().values());

        // our deletion set is the set of Urls in oldUrls that does not exist in new newUrls
        List<String> toDelete = oldUrls.stream().filter(curUrl -> !newUrls.contains(CDN_PATH + "/" + curUrl)).toList();
        log.debug("URLs to delete: " + toDelete);

        // now manipulate our CDN set and then return our results
        // NOTE: Forcing setCdnImages to immediately consume the keyset seems to cause issues if the new set contains
        //       overlapping elements. So we go one by one and add the new elements to the set instead.
        oldPost.getCdnImages().removeIf(curElem -> !toUpdate.getImageUrls().containsKey(curElem));
        oldPost.getCdnImages().addAll(toUpdate.getImageUrls().keySet());
        BlogPreviewDTO toRet = converter.map(blogPostRepo.save(oldPost), BlogPreviewDTO.class);
        return new Pair<>(toRet, toDelete);
    }

    /**
     * Returns a post by a given ID, if it exists. Otherwise, returns null.
     * @param id the id of the post to get
     * @return the post content in DTO format, or null, if it doesn't exist
     */
    public BlogPostDTO getBlogPostById(Long id, boolean includeUnpublished) {
        if(includeUnpublished)
            return blogPostRepo.findById(id).map(blogPost -> converter.map(blogPost, BlogPostDTO.class)).orElse(null);
        else
            return blogPostRepo.findBlogPostByIdAndPublished(id, true).map(blogPost -> converter.map(blogPost, BlogPostDTO.class)).orElse(null);
    }

    /**
     * Deletes a post given its ID. Since there is no removal cascade set, in order for this to work we must individually
     * remove tag connections in order to delete the post.
     * @param id the id of the post to remove
     * @return true if the removal succeeded. false otherwise
     */
    @Transactional
    public boolean removePostById(Long id) {
        try {
            // first get our tags and disassociate them
            Optional<BlogPost> blogPostCont = blogPostRepo.findById(id);
            BlogPost toDelete = blogPostCont.orElseThrow(() -> {return new EmptyResultDataAccessException(1);});
            toDelete.setPostTags(null);
            blogPostRepo.saveAndFlush(toDelete);

            // and then delete the object itself
            blogPostRepo.delete(toDelete);
            return true;
        } catch(EmptyResultDataAccessException e) {
            log.warn(String.format("Attempted deletion of non-present id %d", id));
        } catch(Exception e) {
            log.warn(String.format("Given post with id %d cannot be deleted. Unknown error occurred.", id));
        }

        return false;
    }

    /**
     * In order to speed up this operation a bit, we delegate retrieval to a subset of the post categories.
     * @param id the id of the post to get
     * @return the post preview in DTO format, or null, if it doesn't exist
     */
    public BlogPreviewDTO getBlogPreviewById(Long id) {
        return blogPostRepo.findBlogPostById(id).map(blogPost -> converter.map(blogPost, BlogPreviewDTO.class)).orElse(null);
    }

    /**
     * Returns all the post tags converted to DTO format
     * @return all post tags converted to their DTO object version
     */
    public List<BlogPostTagDTO> getAllPostTags() {
        return blogTagRepo.findAll().stream().map(blogTag -> converter.map(blogTag, BlogPostTagDTO.class)).collect(Collectors.toList());
    }

    /**
     * Attempts to remove a blog post tag by its given ID. Returns false if the given tag is currently in use by a post.
     * @param id the id of the tag to remove
     * @return true if the removal is a success, false otherwise
     */
    public boolean removePostTagById(Long id) {
        try {
            blogTagRepo.deleteById(id);
            return true;
        } catch(EmptyResultDataAccessException e) {
            log.warn(String.format("Attempted deletion of non-present id %d", id));
        } catch(Exception e) {
            log.warn(String.format("Given tag with id %d cannot be deleted. Have you deleted all associated posts?", id));
        }

        return false;
    }

    public boolean setPostPublished(Long id, boolean publish) {
        try {
            Optional<BlogPost> blogPostCont = blogPostRepo.findById(id);
            BlogPost toAdjust = blogPostCont.orElseThrow(() -> {return new EmptyResultDataAccessException(1);});

            toAdjust.setPublished(publish);
            blogPostRepo.save(toAdjust);
            return true;
        } catch(EmptyResultDataAccessException e) {
            log.warn(String.format("Attempted to adjust publish value of invalid post %d", id));
        } catch(Exception e) {
            log.warn(String.format("Given post with id %d cannot be adjusted. Unknown error occurred.", id));
        }

        return false;
    }

    /**
     * Return the number of pages corresponding to all posts with the given unique tag name.
     *
     * @param pageSize the associated page size for the return value
     * @param tagName the tag name to search blog posts for
     * @return the number of pages associated with the potentially returned paged posts
     */
    public Long getNumPages(int pageSize, String tagName, boolean includeUnpublished) {
        Long numPosts;
        if(!includeUnpublished)
            numPosts = blogPostRepo.countBlogPostsByTagName(tagName);
        else
            numPosts = blogPostRepo.countBlogPostsByTagNameWithUnpublished(tagName);

        return (long)Math.ceil(numPosts/(double)pageSize);
    }

    /**
     * The same as above but without any associated tag name to look for
     * @param pageSize the associated page size for the return value
     * @return the total number of pages depending on the total number of available posts
     */
    public Long getNumPages(int pageSize, boolean includeUnpublished) {
        if(includeUnpublished)
            return (long)Math.ceil(blogPostRepo.count()/(double)pageSize);
        else
            return (long)Math.ceil(blogPostRepo.countBlogPostsByPublished(includeUnpublished)/(double)pageSize);
    }

    public List<IdWrapperDTO> getOffsetPagedBlogIds(int pageNumber, int pageSize, String tagName, boolean includeUnpublished) {
        Page<IdWrapper> page;
        if(includeUnpublished)
            page = blogPostRepo.findAllIdsByTagNamePageableOrderedByCreationWithUnpublished(tagName, PageRequest.of(pageNumber, pageSize));
        else
            page = blogPostRepo.findAllIdsByTagNamePageableOrderedByCreation(tagName, PageRequest.of(pageNumber, pageSize));

        // then convert and return
        return page.stream().map(wrapper -> converter.map(wrapper, IdWrapperDTO.class)).collect(Collectors.toList());
    }

    public List<IdWrapperDTO> getOffsetPagedBlogIds(int pageNumber, int pageSize, boolean includeUnpublished) {
        Page<IdWrapper> page;
        if(includeUnpublished)
            page = blogPostRepo.findAllIdsPageableOrderedByCreationWithUnpublished(PageRequest.of(pageNumber, pageSize));
        else
            page = blogPostRepo.findAllIdsPageableOrderedByCreation(PageRequest.of(pageNumber, pageSize));

        // then convert and return
        return page.stream().map(wrapper -> converter.map(wrapper, IdWrapperDTO.class)).collect(Collectors.toList());
    }

    public List<IdWrapperDTO> getAllPostIds() {
        return blogPostRepo.findAllIds().stream().map(curIdElem -> converter.map(curIdElem, IdWrapperDTO.class)).toList();
    }
}
