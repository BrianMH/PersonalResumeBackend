package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A way of keeping track of some attributes of applications. A tag is just a string that is associated with a given
 * group of objects (in this case Applications).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "keyword_tag")
public class ApplicationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // the tag itself is just a string that describes the tag
    @Column(name = "name",
            unique = true,
            nullable = false)
    private String tagName;

    // along with a potential description (which is not necessary)
    @Column(name = "description")
    private String description;

    // a tag is just a string with an associated collection of objects
    // many (tags) -> many (applications) [OWNER]
    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "keywordStore")
    private Set<Application> relevantApplications;

    @Override
    public String toString() {
        return String.format("{ApplicationTag (id=%s) => [tagName=%s] - [description=%s]}",
                this.id, this.tagName, this.description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationTag that = (ApplicationTag) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(tagName, that.tagName) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tagName, description);
    }
}
