package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A generic status DTO that can be returned if necessary
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusDTO {
    @JsonProperty("success")
    boolean success;

    @JsonProperty("status")
    int statusCode;

    @JsonProperty("message")
    String message;
}
