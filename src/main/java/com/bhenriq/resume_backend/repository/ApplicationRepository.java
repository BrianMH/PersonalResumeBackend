package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Provides the basic CRUD functions necessary to interact with the Application table on the database backend.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

}
