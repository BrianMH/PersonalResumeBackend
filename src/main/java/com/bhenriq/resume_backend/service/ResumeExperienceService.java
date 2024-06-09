package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.ResumeExperienceDTO;
import com.bhenriq.resume_backend.repository.ResumeExperienceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeExperienceService {
    @Autowired
    private ResumeExperienceRepository resExpRepo;

    @Autowired
    private ModelMapper converter;

    public List<ResumeExperienceDTO> getAllExperiencePoints() {
        return resExpRepo.findAll().stream().map(expPt -> converter.map(expPt, ResumeExperienceDTO.class)).toList();
    }
}
