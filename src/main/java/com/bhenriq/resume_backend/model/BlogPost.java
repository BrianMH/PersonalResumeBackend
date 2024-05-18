package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

/**
 * Contains all the relevant elements required for a given blogpost. Note that the blog posts need to make connections
 * to the AWS S3 bucket in order to be able to store images.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class BlogPost {
    @SequenceGenerator(name = "blogIdGen", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "blogIdGen")
    private Long id;

    // blog tags
    @ManyToMany
    private Set<BlogPostTag> postTags;

    // blog content
    @Column(name = "headerUrl")
    private String blogHeader;
    @Column(name = "title")
    private String blogTitle;
    @Column(name = "content",
            length = 16777215)  // force MEDIUMTEXT on SQL
    private String blogContent;
    @ElementCollection
    @CollectionTable(
            name = "ImageRef",
            joinColumns = @JoinColumn(name = "blogId")
    )
    private Set<String> cdnImages;

    // post timestamps (these get automatically updated)
    private Instant createdOn;
    private Instant updatedOn;

    @PrePersist
    protected void onPersist() {
        this.createdOn = Instant.now();
        this.updatedOn = this.createdOn;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = Instant.now();
    }

    @Override
    public String toString() {
        return String.format("{BlogPost (id=%d) == [header = %s] + [title = %s] + [content = %s] + [tags = %s] [created = %s] + [updated = %s] }",
                id, blogHeader, blogTitle, blogContent, postTags, createdOn, updatedOn);
    }
}
