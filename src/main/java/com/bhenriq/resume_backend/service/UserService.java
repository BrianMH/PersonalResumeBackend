package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.RoleDTO;
import com.bhenriq.resume_backend.dto.TokenUpdateDTO;
import com.bhenriq.resume_backend.dto.UserDTO;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.exception.UpdateException;
import com.bhenriq.resume_backend.model.Account;
import com.bhenriq.resume_backend.model.EmailWhitelist;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.AccountRepository;
import com.bhenriq.resume_backend.repository.EmailWhitelistRepository;
import com.bhenriq.resume_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Exposes basic management methods for the user data object
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private EmailWhitelistRepository emailListRepo;

    @Autowired
    private ModelMapper converter;

    public UserDTO getUserFromId(String id) {
        // first see if we can load the relevant user
        Optional<User> relUser = userRepo.findById(id);

        // and then convert it to a relevant DTO type for return
        return relUser.map(user -> converter.map(user, UserDTO.class)).orElse(null);
    }

    /**
     * Attempts to update a given user given a specific id and DTO containing all elements to update. If an element matches
     * the contents of what's already in the server, then it is simply left alone. (Idempotency of PUT)
     *
     * Returns true after a successful operation, and false, otherwise (note that false is only returned if the specified
     * resource does not exist)
     * @param id the id of the user to adjust
     * @param updatePatch contains all current fields to replace the old values with on mismatch
     * @return the updated user object, if update was performed
     */
    public UserDTO updateUserFromId(String id, UserDTO updatePatch) {
        // first grab our relevant user
        Optional<User> relUser = userRepo.findById(id);
        if(relUser.isEmpty())
            return null;

        // rely on good faith that the patch is valid and accordingly update all elements
        // to retain idempotency, make sure the values are different before changing them in the object
        User user = relUser.get();
        boolean changeDetected = false;

        if(!user.getImage().equals(updatePatch.getImage())) {
            user.setImage(updatePatch.getImage());
            changeDetected = true;
            }
        if(!user.getEmail().equals(updatePatch.getEmail()))
            throw new UpdateException("E-mails cannot be changed.");
        if(!user.getUsername().equals(updatePatch.getUsername())) {
            user.setUsername(updatePatch.getUsername());
            changeDetected = true;
        }

        User changedUser = user;
        if(changeDetected)
            changedUser = userRepo.save(user);

        return converter.map(changedUser, UserDTO.class);
    }

    public boolean deleteUserFromId(String id) {
        if(userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to update an associated user's tokens given the old values.
     *
     * @param updatePatch
     * @return
     */
    public boolean updateTokensFromAccount(TokenUpdateDTO updatePatch) {
        // first make sure the user exists
        Optional<User> relevantUser = userRepo.findById(updatePatch.getUserId());
        if(relevantUser.isEmpty())
            throw new NotFoundException("User does not exist");

        // then continue the search for the relevant account
        Set<Account> allUserAccounts = relevantUser.get().getAccounts();
        List<Account> relevantAccounts = allUserAccounts.stream().filter(account -> {
            // make sure accounts have the relevant fields equivalent to the patch
            return account.getAccessToken().equals(updatePatch.getOldAccessToken()) &&
                    account.getTokenExpiry().equals(updatePatch.getOldExpiresAt());
        }).toList();

        // make sure there's only one matching account before continuing
        if(relevantAccounts.size() > 1)
            throw new RuntimeException("Encountered duplicate access tokens and expiry elements. Ensure database is properly set up.");
        else if (relevantAccounts.isEmpty()) {
            throw new NotFoundException(String.format("No given user exists with passed token %s", updatePatch.getOldAccessToken()));
        }
        Account relevantAccount = relevantAccounts.getFirst();
        relevantAccount.setTokenExpiry(updatePatch.getNewExpiresAt());
        relevantAccount.setAccessToken(updatePatch.getNewAccessToken());
        accRepo.save(relevantAccount);
        return true;
    }

    public UserDTO getUserFromEmail(String email) {
        // find the user
        Optional<User> relUser = userRepo.findUserByEmail(email);

        // and convert it
        return relUser.map(user -> converter.map(user, UserDTO.class)).orElse(null);
    }

    public UserDTO getUserFromAccount(String provider, String providerId) {
        // find the user
        Optional<User> relUser = accRepo.findAccountByProviderAndProviderAccountId(provider, providerId).map(Account::getOwningUser);

        // and return it as a DTO
        return relUser.map(user -> converter.map(user, UserDTO.class)).orElse(null);
    }

    public List<RoleDTO> getUserRolesFromId(String id) {
        // first try loading the relevant user
        Optional<User> relUser = userRepo.findById(id);

        // and then convert our relevant roles for return
        if(relUser.isPresent())
            System.out.println(relUser.get().getRoles());
        else
            System.out.println("Could not find a user with UUID - " + id);
        return relUser.map(user -> user.getRoles().stream().map(role -> converter.map(role, RoleDTO.class)).toList()).orElse(new ArrayList<>());
    }

    public UserDTO createNewUser(UserDTO toAdd) {
        // map to true object and attempt save
        User copiedUser = null;
        try {
            User realAddObj = converter.map(toAdd, User.class);

            // before we create the new user, we can use the user e-mail white-list in order to pre-determine some roles
            // that should be in place.
            realAddObj.setRoles(emailListRepo.findById(toAdd.getEmail()).map(EmailWhitelist::getRoles).orElse(null));
            copiedUser = userRepo.save(realAddObj);
        } catch (Exception err) {
            err.printStackTrace();
            log.debug("Could not create new user with DTO user obj: " + toAdd);
        }

        return converter.map(copiedUser, UserDTO.class);
    }
}
