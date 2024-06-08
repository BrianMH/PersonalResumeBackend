package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.ResumeSkillDTO;
import com.bhenriq.resume_backend.repository.ResumeSkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeSkillService {
    @Autowired
    private ResumeSkillRepository resumeSkillRepo;

    @Autowired
    private ModelMapper converter;

    public List<ResumeSkillDTO> getSkillsWithType(String skillType) {
        return resumeSkillRepo.findAllByType(skillType).stream().map(curSkill -> converter.map(curSkill, ResumeSkillDTO.class)).toList();
    }
}
