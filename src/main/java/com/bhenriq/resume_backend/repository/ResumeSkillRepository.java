package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ResumeSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, String> {
    List<ResumeSkill> findAllByType(String skillType);
}
