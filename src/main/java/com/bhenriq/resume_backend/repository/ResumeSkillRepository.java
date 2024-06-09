package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ResumeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, String> {
    List<ResumeSkill> findAllByTypeIgnoreCase(String skillType);

    @Query(
            "SELECT DISTINCT r.type FROM ResumeSkill r"
    )
    List<String> findAllTypes();
}
