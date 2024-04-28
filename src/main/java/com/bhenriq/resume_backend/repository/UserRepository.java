package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("FROM User u JOIN Account a WHERE a.accessToken=:accessToken AND a.provider=:provider")
    Optional<User> findUserByAccountsAccessToken(@Param("provider") String provider, @Param("accessToken") String accessToken);

    Optional<User> findUserByEmail(String email);
}
