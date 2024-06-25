package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ResumeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeProjectRepository extends JpaRepository<ResumeProject, String> {

}
