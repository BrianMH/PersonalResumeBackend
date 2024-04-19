package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.ApplicationDTO;
import com.bhenriq.resume_backend.model.Application;
import com.bhenriq.resume_backend.repository.ApplicationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provides the general utility functions that would be converting between DTOs and real application objects in the
 * application.
 */
@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private ModelMapper converter;

    public List<ApplicationDTO> getAllApplications() {
        // first get all our Applications
        List<Application> relApps = appRepo.findAll();

        // then convert into DTO format and return
        return relApps.stream().map(app -> converter.map(app, ApplicationDTO.class)).collect(Collectors.toList());
    }

    public ApplicationDTO findByApplicationId(UUID id) {
        // first get potential application and convert it
        Optional<Application> relApp = appRepo.findById(id);
        Optional<ApplicationDTO> convertedApp = relApp.map(app -> converter.map(app, ApplicationDTO.class));

        // and then we can return or throw an error if not found
        // TODO: Change this into something that is some error subclass
        return convertedApp.orElseThrow(() -> new RuntimeException("Error has occurred"));
    }
}
