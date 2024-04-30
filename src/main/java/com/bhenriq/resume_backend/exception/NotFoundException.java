package com.bhenriq.resume_backend.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

public class NotFoundException extends RestRuntimeException {
    private static final int NOT_FOUND_CODE = HttpServletResponse.SC_NOT_FOUND;

    public NotFoundException(String message) {
        super(NOT_FOUND_CODE, message);
    }
}
