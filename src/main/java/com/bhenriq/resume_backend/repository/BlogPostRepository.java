package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.BlogPost;
import com.bhenriq.resume_backend.model_projections.BlogPreview;
import com.bhenriq.resume_backend.model_projections.IdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPreview> findBlogPostById(Long id);
    Optional<BlogPost> findBlogPostByIdAndPublished(Long id, Boolean published);

    @Query(
            "SELECT COUNT(*) FROM BlogPost b JOIN b.postTags t WHERE LOWER(t.tagName) = LOWER(:query) AND b.published = true "
    )
    Long countBlogPostsByTagName(@Param("query")String query);

    Long countBlogPostsByPublished(Boolean published);

    @Query(
            "SELECT COUNT(*) FROM BlogPost b JOIN b.postTags t WHERE LOWER(t.tagName) = LOWER(:query) "
    )
    Long countBlogPostsByTagNameWithUnpublished(@Param("query")String query);

    @Query(
            "FROM BlogPost b JOIN b.postTags t WHERE LOWER(t.tagName) = LOWER(:query) AND b.published = true ORDER BY b.createdOn DESC "
    )
    Page<IdWrapper> findAllIdsByTagNamePageableOrderedByCreation(@Param("query")String query, Pageable pageable);

    @Query(
            "FROM BlogPost b JOIN b.postTags t WHERE LOWER(t.tagName) = LOWER(:query) ORDER BY b.createdOn DESC "
    )
    Page<IdWrapper> findAllIdsByTagNamePageableOrderedByCreationWithUnpublished(@Param("query")String query, Pageable pageable);

    @Query(
            "FROM BlogPost b WHERE b.published = true ORDER BY b.createdOn DESC "
    )
    Page<IdWrapper> findAllIdsPageableOrderedByCreation(Pageable pageable);

    @Query(
            "FROM BlogPost b ORDER BY b.createdOn DESC "
    )
    Page<IdWrapper> findAllIdsPageableOrderedByCreationWithUnpublished(Pageable pageable);

    @Query(
            "FROM BlogPost b WHERE b.published = true"
    )
    List<IdWrapper> findAllIds();

    @Query(
        "FROM BlogPost b"
    )
    List<IdWrapper> findAllIdsWithUnpublished();
}
