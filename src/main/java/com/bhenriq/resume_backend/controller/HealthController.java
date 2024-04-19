package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unlike other controllers with a purpose, this controller lies on the root of the API path in order
 * to let other devices know whether the server is up.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping
    public ResponseEntity<StatusDTO> getServerStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDTO(true, "OK"));
    }
}
