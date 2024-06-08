package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.ResumeSkillDTO;
import com.bhenriq.resume_backend.service.ResumeSkillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implements most of the methods that need to feed into the resume page in order to properly render the page
 * dynamically.
 */
@RestController
@RequestMapping("/api/resume")
@Slf4j
public class ResumeController {
    @Autowired
    private ResumeSkillService resumeSkillService;

    @GetMapping("/skills/type/{skillType}")
    public ResponseEntity<List<ResumeSkillDTO>> getAllTechnicalSkills(@PathVariable("skillType") String skillType) {
        List<ResumeSkillDTO> relevantSkills = resumeSkillService.getSkillsWithType(skillType);

        return ResponseEntity.status(HttpStatus.SC_OK).body(relevantSkills);
    }
}
