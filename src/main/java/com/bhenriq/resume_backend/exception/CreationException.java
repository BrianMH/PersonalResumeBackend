package com.bhenriq.resume_backend.exception;

import jakarta.servlet.http.HttpServletResponse;

public class CreationException extends RestRuntimeException {
    public CreationException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
}
