package com.bhenriq.resume_backend.repository;

import com.bhenriq.resume_backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findAccountByAccessTokenAndProvider(String accessToken, String provider);

    Optional<Account> findAccountByProviderAndProviderAccountId(String provider, String providerAccountId);
}
