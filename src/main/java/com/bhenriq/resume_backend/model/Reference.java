package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Test;

import java.util.Objects;

/**
 * A reference contains a link to a specific type of reference relevant to the associated element. For now, this is
 * only associated with experience points and is used to identify the icon used and the tooltip to use.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // reference urls should not overlap
    private String refUrl;
    private String iconType;
    private String description;

    // Used to define typical constants used for special icons
    public enum IconTypes {
        GITHUB_ICON,
        BOOK_ICON,
        WEB_ICON
    }

    @Override
    public String toString() {
        return "Reference{" +
                "id='" + id + '\'' +
                ", refUrl='" + refUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reference reference = (Reference) o;
        return Objects.equals(id, reference.id) && Objects.equals(refUrl, reference.refUrl) && Objects.equals(description, reference.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, refUrl, description);
    }
}
