package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceRepository extends JpaRepository<Reference, String> {

}
