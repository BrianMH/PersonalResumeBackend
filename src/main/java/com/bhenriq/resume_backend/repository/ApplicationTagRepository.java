package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.ApplicationTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationTagRepository extends JpaRepository<ApplicationTag, UUID> {

}
