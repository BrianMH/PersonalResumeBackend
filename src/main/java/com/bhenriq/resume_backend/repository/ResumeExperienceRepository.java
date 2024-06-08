package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ResumeExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, String> {

}
