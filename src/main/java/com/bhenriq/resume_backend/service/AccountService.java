package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.AccountDTO;
import com.bhenriq.resume_backend.model.Account;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.AccountRepository;
import com.bhenriq.resume_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Defines some of the more common methods to be used to parts of the application that depend on the Account objects.
 */
@Service
public class AccountService {
    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper converter;

    /**
     * A helper function used to return the base user given a particular account via it's single-sided connection.
     *
     * @param accessToken
     * @param provider
     * @return
     */
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
    public boolean updateAccountToken(AccountDTO toChange) {
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

    public AccountDTO associateAccountToUser(AccountDTO toLink, String userId) {
        // first make sure the user to link to actually exists
        if(!userRepo.existsById(userId))
            return null;

        // and then attempt linking via account creation
        Account toAdd = converter.map(toLink, Account.class);
        toAdd.setOwningUser(userRepo.getReferenceById(userId));
        Account savedAccount = accRepo.save(toAdd);

        return converter.map(savedAccount, AccountDTO.class);
    }
}
