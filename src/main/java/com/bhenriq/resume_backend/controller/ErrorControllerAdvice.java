package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.StatusDTO;
import com.bhenriq.resume_backend.exception.RestRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles all relevant errors that arise from any of the REST APIs present that use the given RestRuntimeException
 * subclasses.
 */
@RestControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestRuntimeException.class)
    public ResponseEntity<StatusDTO> handleRestRuntimeException(HttpServletRequest req, RestRuntimeException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getCode());
        return new ResponseEntity<>(new StatusDTO(false, exception.getCode(), exception.getMessage()), status);
    }
}
