package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

/**
 * Contains the experience points of the resume page
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ResumeExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "type")
    private String type;
    @Column(name = "title")
    private String title;
    @Column(name = "location")
    private String location;

    // For dates, we consider keeping timestamps but will only ever render the month and year
    // Note that ended can be null (which would mean the position is currently taking place)
    @Column(name = "started",
            nullable = false)
    private LocalDate started;
    @Column(name = "ended")
    private LocalDate ended;

    // And we keep our experience bullets in a list
    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable(
            name = "ExperiencePoints",
            joinColumns = @JoinColumn(name = "resExpId")
    )
    private List<String> bullets;

    // and any associated references to be used for the given item
    @OneToMany(
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    private List<Reference> references;
}
