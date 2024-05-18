package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * A tag used to categorize blog posts. Completely symbolic.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class BlogPostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String tagName;

    @ManyToMany(mappedBy = "postTags",
                fetch = FetchType.LAZY)
    private List<BlogPost> relPosts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogPostTag that = (BlogPostTag) o;
        return id.equals(that.id) && Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tagName);
    }

    @Override
    public String toString() {
        return String.format("{ BlogTag (id=%d) (name=%s) }", this.id, this.tagName);
    }
}
