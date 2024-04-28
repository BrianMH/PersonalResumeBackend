package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.AccountDTO;
import com.bhenriq.resume_backend.model.Account;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Defines some of the more common methods to be used to parts of the application that depend on the Account objects.
 */
@Service
public class AccountService {
    @Autowired
    private AccountRepository accRepo;

    public User getBaseUserFromAccessTokenAndProvider(String accessToken, String provider) {
        // first find the relevant account
        Optional<Account> relAccount = accRepo.findAccountByAccessTokenAndProvider(accessToken, provider);

        // if the account exists then we only need the related user
        return relAccount.map(Account::getOwningUser).orElse(null);
    }

    /**
     * Attempts to change a user's given access token (account specific). If the change succeeds, then return true,
     * false otherwise. The uniquely identifying elements would be the OAuth provider along with the specified
     * provider account id.
     * @param toChange
     * @return
     */
    public boolean updateUserToken(AccountDTO toChange) {
        // first try to find the relevant account given inputs
        Optional<Account> relAccount = accRepo.findAccountByProviderAndProviderAccountId(toChange.getProvider(), toChange.getProviderAccountId());
        if(relAccount.isEmpty())
            return false;

        // and then change the elements we want from the account
        Account changeObj = relAccount.get();
        changeObj.setAccessToken(toChange.getAccessToken());

        // and then push it back to the database for update
        accRepo.save(changeObj);
        return true;
    }
}
