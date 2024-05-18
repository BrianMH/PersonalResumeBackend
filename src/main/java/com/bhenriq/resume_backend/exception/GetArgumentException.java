package com.bhenriq.resume_backend.exception;

import jakarta.servlet.http.HttpServletResponse;

public class GetArgumentException extends RestRuntimeException {
    public GetArgumentException(String message) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
}
