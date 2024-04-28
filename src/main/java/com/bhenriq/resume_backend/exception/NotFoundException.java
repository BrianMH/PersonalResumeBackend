package com.bhenriq.resume_backend.exception;

import lombok.RequiredArgsConstructor;

public class NotFoundException extends RestRuntimeException {
    public NotFoundException(int code, String message) {
        super(code, message);
    }
}
