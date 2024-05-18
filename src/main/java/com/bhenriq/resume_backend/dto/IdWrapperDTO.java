package com.bhenriq.resume_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Allows transfer of a sequence of object IDs, irrespective of the relevant ID type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdWrapperDTO {
    @JsonProperty("id")
    private String id;

    @Override
    public String toString() {
        return "{IdWrapperDTO - (id = " + id + " )}";
    }
}
