package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.AccountDTO;
import com.bhenriq.resume_backend.dto.StatusDTO;
import com.bhenriq.resume_backend.dto.TokenUpdateDTO;
import com.bhenriq.resume_backend.dto.UserDTO;
import com.bhenriq.resume_backend.exception.CreationException;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.exception.UpdateException;
import com.bhenriq.resume_backend.service.AccountService;
import com.bhenriq.resume_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Because users and accounts are heavily tied to one another, we have a controller that manipulates both here instead of
 * providing separate controllers to keep similar logic in the same place.
 */
@RequestMapping("/api/users")
@RestController
public class UserAccountController {
    @Autowired
    private UserService userSvc;

    @Autowired
    private AccountService accSvc;

    /**
     * This method updates an access token for a given user given their UUID. That means the only
     * populated elements from the DTO will be the user's UUID and the user's new access token.
     *
     * @param accountToAdjust A DTO containing the two fields listed above
     * @return a status DTO with the corresponding success or failure along with the corresponding HTTP status response
     */
    @PostMapping("/updateToken")
    public ResponseEntity<StatusDTO> changeAccountAccessToken(@RequestBody AccountDTO accountToAdjust) {
        boolean valueReplaced = accSvc.updateAccountToken(accountToAdjust);
        if(!valueReplaced) {
            throw new NotFoundException("Error updating specified account's access code.");
        } else {
            return ResponseEntity.ok().body(new StatusDTO(true, HttpServletResponse.SC_OK, "Access code changed."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserViaUuid(@PathVariable String id) {
        UserDTO toReturn = userSvc.getUserFromId(id);
        if(toReturn == null) {
            throw new NotFoundException("Given user does not exist.");
        } else {
            toReturn.setRoleTypeImplicitly();
            return ResponseEntity.ok().body(toReturn);
        }
    }

    @PutMapping("/{id}/account")
    public ResponseEntity<AccountDTO> linkAccountToUser(@PathVariable("id") String userId, @RequestBody AccountDTO toLink) {
        AccountDTO toReturn = accSvc.associateAccountToUser(toLink, userId);
        if(toReturn == null) {
            throw new NotFoundException("Given user does not exist.");
        } else {
            return ResponseEntity.ok().body(toReturn);
        }
    }

    /**
     * For now deletes are left unimplemented as they are unused by Next Auth currently.
     * @param id the id of the user to delete
     * @return a DTO representing the success status of the operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StatusDTO> deleteUserViaUuid(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new StatusDTO(false, HttpServletResponse.SC_NOT_IMPLEMENTED, "Unimplemented"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserViaUuid(@PathVariable String id, @RequestBody UserDTO toUpdate) {
        UserDTO updatedUser = userSvc.updateUserFromId(id, toUpdate);

        if(updatedUser == null) {
            throw new NotFoundException("Given user does not exist.");
        } else {
            updatedUser.setRoleTypeImplicitly();
            return ResponseEntity.ok().body(updatedUser);
        }
    }

    @PostMapping("/{id}/update_token")
    public ResponseEntity<StatusDTO> updateUserTokenViaUuid(@PathVariable String id, @RequestBody TokenUpdateDTO toUpdate) {
        boolean accountChanged = userSvc.updateTokensFromAccount(toUpdate);

        if(accountChanged)
            return ResponseEntity.ok().body(new StatusDTO(true, HttpServletResponse.SC_OK, "Updated token"));
        else
            throw new UpdateException("No token update performed.");
    }

    @GetMapping("/email/{relEmail}")
    public ResponseEntity<UserDTO> getUserViaEmail(@PathVariable("relEmail") String email) {
        UserDTO toReturn = userSvc.getUserFromEmail(email);
        if(toReturn == null) {
            throw new NotFoundException("Given user does not exist.");
        } else {
            toReturn.setRoleTypeImplicitly();
            return ResponseEntity.ok().body(toReturn);
        }
    }

    @GetMapping("/account/{provider}/{providerId}")
    public ResponseEntity<UserDTO> getUserViaAccount(@PathVariable String provider, @PathVariable String providerId) {
        UserDTO toReturn = userSvc.getUserFromAccount(provider, providerId);
        if(toReturn == null) {
            throw new NotFoundException("Given user does not exist.");
        } else {
            toReturn.setRoleTypeImplicitly();
            return ResponseEntity.ok().body(toReturn);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO toRecord) {
        // try saving the object to the database first
        UserDTO toReturn = userSvc.createNewUser(toRecord);
        if(toReturn == null) {
            throw new CreationException("Failed to create new user.");
        } else {
            toReturn.setRoleTypeImplicitly();
            return ResponseEntity.status(HttpStatus.CREATED).body(toReturn);
        }
    }
}
