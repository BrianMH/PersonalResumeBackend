package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.EmailWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailWhitelistRepository extends JpaRepository<EmailWhitelist, String> {
}
