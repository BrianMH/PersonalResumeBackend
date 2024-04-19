package com.bhenriq.resume_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a submitted application, which may include some relevant keywords, perhaps also brief information
 * regarding the position, and also essential metadata including the relevant company, the job title, potential payment
 * information, application submission date (if necessary), and so forth...
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Application specifics
    @Column(name = "job_title",
            nullable = false)
    private String jobTitle;
    @Column(name = "company_name",
            nullable = false)
    private String companyName;
    @Column(name = "app_url",
            nullable = false)
    private String applicationUrl;      // this isn't quite reliable due to the nature of applications expiring!
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "application_keyword_tag",
               joinColumns = @JoinColumn(name = "application_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<ApplicationTag> keywordStore;      // represents keywords that might be relevant to application

    // Application metadata
    @Column(name = "apply_time")
    private Instant applyTime;

    @Column(name = "response_time")
    private Instant responseTime;

    @Override
    public String toString() {
        return String.format("{Application (id=%s) => [jobTitle=%s] - [companyName=%s] - [appUrl=%s] - " +
                             "[keywordStore-%s] - [applyTime-%s] - [responseTime-%s]}",
                this.id, this.jobTitle, this.companyName, this.applicationUrl, this.keywordStore.toString(),
                this.applyTime, this.responseTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(companyName, that.companyName) &&
                Objects.equals(applicationUrl, that.applicationUrl) &&
                Objects.equals(applyTime, that.applyTime) &&
                Objects.equals(responseTime, that.responseTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobTitle, companyName, applicationUrl);
    }
}
