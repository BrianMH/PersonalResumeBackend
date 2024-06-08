package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Implements a basic name -> value pair that is representative of a technical or soft skill. The skillType variable holds
 * the actual skill type, and most searches would be done with respect to the type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        indexes = {
                @Index(columnList = "type", name = "type_hidx")
        }
)
public class ResumeSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name",
            nullable = false)
    private String name;

    @Column(name = "value",
            nullable = false)
    private Integer value;

    @Column(name = "type",
            nullable = false)
    private String type;

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", barVal=" + value +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeSkill resumeSkill = (ResumeSkill) o;
        return Objects.equals(id, resumeSkill.id) && Objects.equals(name, resumeSkill.name) && Objects.equals(value, resumeSkill.value) && Objects.equals(type, resumeSkill.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value, type);
    }
}