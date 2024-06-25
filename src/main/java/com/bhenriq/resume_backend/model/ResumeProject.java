package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Container for the ResumeProject object. The object represents an entry in the resume that is a prior project of
 * interest. Due to the CV format of the projects section, there can be quite a few personal projects and
 * old team projects mixed together.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ResumeProject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String shortDescription;
    @Column(nullable = false)
    private String projectRole;
    @Column(nullable = false)
    private String projectType;     // a project type represents whether it was coursework or a personal project

    @Column(name = "started",
            nullable = false)
    private LocalDate started;
    @Column(name = "ended")
    private LocalDate ended;

    // And we keep our experience bullets in a list
    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable(
            name = "ResumeProjectPoints",
            joinColumns = @JoinColumn(name = "resProjId")
    )
    private List<String> bullets;

    // and any associated references to be used for the given item
    @OneToMany(
            fetch = FetchType.EAGER
    )
    private List<Reference> references;
}
