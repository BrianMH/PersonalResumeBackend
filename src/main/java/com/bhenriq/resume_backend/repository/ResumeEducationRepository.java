package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ResumeEducation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, String> {

}
