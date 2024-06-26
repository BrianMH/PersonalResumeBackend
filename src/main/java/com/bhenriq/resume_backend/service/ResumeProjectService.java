package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.ResumeProjectDTO;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.exception.UpdateException;
import com.bhenriq.resume_backend.model.Reference;
import com.bhenriq.resume_backend.model.ResumeProject;
import com.bhenriq.resume_backend.repository.ReferenceRepository;
import com.bhenriq.resume_backend.repository.ResumeProjectRepository;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeProjectService {
    @Autowired
    private ResumeProjectRepository resumeProjectRepo;

    @Autowired
    private ReferenceRepository referenceRepo;

    @Autowired
    private ModelMapper converter;

    public List<ResumeProjectDTO> getAllProjects() {
        return resumeProjectRepo.findAll().stream().map(proj -> converter.map(proj, ResumeProjectDTO.class)).toList();
    }

    public ResumeProjectDTO getProjectById(String id) {
        return converter.map(resumeProjectRepo.findById(id).orElse(null), ResumeProjectDTO.class);
    }

    public List<String> getAllIds() {
        return resumeProjectRepo.findAll().stream().map(ResumeProject::getId).toList();
    }

    public ResumeProjectDTO addProject(ResumeProjectDTO toAdd) {
        // first convert our object
        ResumeProject toSave = converter.map(toAdd, ResumeProject.class);

        // and save the references before saving the actual value
        referenceRepo.saveAll(toSave.getReferences());
        ResumeProject savedValue = resumeProjectRepo.save(toSave);

        // and then return the DTO
        return converter.map(savedValue, ResumeProjectDTO.class);
    }

    public void deleteProject(String id) {
        Optional<ResumeProject> toDeleteCnt = resumeProjectRepo.findById(id);
        ResumeProject toDelete = toDeleteCnt.orElseThrow(() -> new NotFoundException("Project with id " + id + " does not exist."));

        // disconnect entries with references before deletion
        List<Reference> staleRefs = toDelete.getReferences();
        toDelete.setReferences(null);
        resumeProjectRepo.saveAndFlush(toDelete);
        referenceRepo.deleteAll(staleRefs);
        resumeProjectRepo.delete(toDelete);
    }

    public ResumeProjectDTO updateProject(ResumeProjectDTO toUpdate) {
        // first we get our old value to make sure it exists
        ResumeProject oldValues = resumeProjectRepo.findById(toUpdate.getId()).orElseThrow(() -> new NotFoundException("Indicated post does not exist."));
        List<Reference> toDelete = oldValues.getReferences();   // this gets used later to evaluate which references to delete when orphaned

        // we can then update all our entries
        ResumeProject newProjValue = converter.map(toUpdate, ResumeProject.class);

        // we need to adjust our references
        List<Reference> savedRefs = referenceRepo.saveAll(newProjValue.getReferences());
        newProjValue.setReferences(null);   // this needs to be emptied so it doesn't hold unassociated references
        ResumeProject savedValue = resumeProjectRepo.save(newProjValue);
        savedValue.setReferences(savedRefs);
        savedValue = resumeProjectRepo.save(savedValue); // update with references

        // and delete any orphans due to the way we manage references
        System.out.println(toDelete);
        toDelete.removeIf(oldRef -> savedRefs.stream().anyMatch(newRef -> oldRef.getRefUrl().equals(newRef.getRefUrl())));
        System.out.println(toDelete);
        referenceRepo.deleteAll(toDelete);

        // and finally return our converted values
        return converter.map(savedValue, ResumeProjectDTO.class);
    }
}
