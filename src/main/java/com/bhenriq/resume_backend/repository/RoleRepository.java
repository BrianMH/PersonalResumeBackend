package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

}
