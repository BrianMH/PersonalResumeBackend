package com.bhenriq.resume_backend.exception;

import jakarta.servlet.http.HttpServletResponse;

public class UpdateException extends RestRuntimeException {
    public UpdateException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}
