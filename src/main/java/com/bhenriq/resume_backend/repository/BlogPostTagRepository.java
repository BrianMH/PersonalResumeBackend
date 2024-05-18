package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.BlogPostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostTagRepository extends JpaRepository<BlogPostTag, Long> {
    Optional<BlogPostTag> findByTagNameIgnoreCase(String tagName);
}
