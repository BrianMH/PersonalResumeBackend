package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.ApplicationDTO;
import com.bhenriq.resume_backend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Manages REST requests for the Application store
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService appService;

    @GetMapping("/all")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return ResponseEntity.status(HttpStatus.OK).body(appService.getAllApplications());
    }
}
