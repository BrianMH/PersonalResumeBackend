package com.bhenriq.resume_backend.model_projections;

/**
 * Can be used to project to the ID variable of any class who uses the attribute ID as the entity id.
 */
public interface IdWrapper {
    Long getId();
}
