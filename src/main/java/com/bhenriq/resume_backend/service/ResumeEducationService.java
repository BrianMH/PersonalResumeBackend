package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.ResumeEducationDTO;
import com.bhenriq.resume_backend.repository.ResumeEducationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeEducationService {
    @Autowired
    private ResumeEducationRepository resEduRepo;

    @Autowired
    private ModelMapper converter;

    public List<ResumeEducationDTO> getAllEducationPoints() {
        return resEduRepo.findAll().stream().map(eduPt -> converter.map(eduPt, ResumeEducationDTO.class)).toList();
    }
}
