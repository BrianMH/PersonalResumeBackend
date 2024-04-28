package com.bhenriq.resume_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class RestRuntimeException extends RuntimeException {
    private final int code;
    private final String message;
}
