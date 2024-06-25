package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.*;
import com.bhenriq.resume_backend.exception.CreationException;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.exception.UpdateException;
import com.bhenriq.resume_backend.service.ResumeEducationService;
import com.bhenriq.resume_backend.service.ResumeExperienceService;
import com.bhenriq.resume_backend.service.ResumeProjectService;
import com.bhenriq.resume_backend.service.ResumeSkillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ResumeEducationService resumeEducationService;

    @Autowired
    private ResumeExperienceService resumeExperienceService;

    @Autowired
    private ResumeProjectService resumeProjectService;

    /*
        SKILL METHODS
     */
    @GetMapping("/skills/type/{skillType}")
    public ResponseEntity<List<ResumeSkillDTO>> getAllTechnicalSkills(@PathVariable("skillType") String skillType) {
        List<ResumeSkillDTO> relevantSkills = resumeSkillService.getSkillsWithType(skillType);

        return ResponseEntity.status(HttpStatus.SC_OK).body(relevantSkills);
    }

    @GetMapping("/skills/type")
    public ResponseEntity<List<String>> getAllSkillTypes() {
        List<String> allSkillTypes = resumeSkillService.getAllSkillTypes();

        return ResponseEntity.status(HttpStatus.SC_OK).body(allSkillTypes);
    }

    /*
        EDUCATION METHODS
     */
    @GetMapping("/education")
    public ResponseEntity<List<ResumeEducationDTO>> getAllEducationPoints() {
        List<ResumeEducationDTO> allEduPts = resumeEducationService.getAllEducationPoints();

        return ResponseEntity.status(HttpStatus.SC_OK).body(allEduPts);
    }

    /*
        EXPERIENCE METHODS
     */
    @GetMapping("/experience")
    public ResponseEntity<List<ResumeExperienceDTO>> getAllExperiencePoints() {
        List<ResumeExperienceDTO> allExpPts = resumeExperienceService.getAllExperiencePoints();

        return ResponseEntity.status(HttpStatus.SC_OK).body(allExpPts);
    }

    /*
        PROJECT METHODS
     */
    @GetMapping("/project")
    public ResponseEntity<List<ResumeProjectDTO>> getAllProjectPoints() {
        List<ResumeProjectDTO> allProjPts = resumeProjectService.getAllProjects();

        return ResponseEntity.status(HttpStatus.SC_OK).body(allProjPts);
    }

    @PostMapping("/project/new")
    public ResponseEntity<ResumeProjectDTO> createResumeProject(@RequestBody ResumeProjectDTO toAdd) {
        // make sure the backend is setting the id
        if(toAdd.getId() != null)
            throw new CreationException("Created object must not have an assigned id.");

        ResumeProjectDTO addedProject = resumeProjectService.addProject(toAdd);

        return ResponseEntity.status(HttpStatus.SC_OK).body(addedProject);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<ResumeProjectDTO> getProjectById(@PathVariable("id") String id) {
        ResumeProjectDTO relProj = resumeProjectService.getProjectById(id);

        if(relProj == null)
            throw new NotFoundException("Project with id " + id + " does not exist");
        else
            return ResponseEntity.status(HttpStatus.SC_OK).body(relProj);
    }

    @PostMapping("/project/{id}")
    public ResponseEntity<ResumeProjectDTO> updateProjectById(@PathVariable("id") String id, @RequestBody ResumeProjectDTO toUpdate) {
        // make sure the update parameters are followed
        if(!id.equals(toUpdate.getId()))
            throw new UpdateException("Id of project and object to copy values from must match.");

        ResumeProjectDTO updatedProject = resumeProjectService.updateProject(toUpdate);

        return ResponseEntity.status(HttpStatus.SC_OK).body(updatedProject);
    }

    @GetMapping("/project/index")
    public ResponseEntity<List<String>> getAllProjectIds() {
        List<String> relIds = resumeProjectService.getAllIds();

        return ResponseEntity.status(HttpStatus.SC_OK).body(relIds);
    }
}
