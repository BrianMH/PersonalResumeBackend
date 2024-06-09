package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.YearMonth;
import java.util.Set;

/**
 * Contains the education points of the resume page
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class ResumeEducation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "location",
            nullable = false)
    private String location;
    @Column(name = "degreeType",
            nullable = false)
    private String degreeType;
    @Column(name = "degreeTitle",
            nullable = false)
    private String degreeTitle;
    @Column(name = "gpa")
    private Double gpa;
    @Column(name = "focus")
    private String focus;

    // For dates, we consider keeping timestamps but will only ever render the month and year
    // Note that ended can be null (which would mean the position is currently taking place)
    @Column(name = "started",
            nullable = false)
    private LocalDate started;
    @Column(name = "ended")
    private LocalDate ended;

    // And we keep our relevant topics in a list
    // TODO: This could avoid repetition by having a "topics" table, but the amount of saved space is likely negligible
    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable(
            name = "EducationTopics",
            joinColumns = @JoinColumn(name = "resEduId")
    )
    private Set<String> topics;
}
