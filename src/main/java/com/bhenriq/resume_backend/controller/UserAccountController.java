package com.bhenriq.resume_backend.controller;

import com.bhenriq.resume_backend.dto.AccountDTO;
import com.bhenriq.resume_backend.dto.StatusDTO;
import com.bhenriq.resume_backend.dto.UserDTO;
import com.bhenriq.resume_backend.exception.NotFoundException;
import com.bhenriq.resume_backend.service.AccountService;
import com.bhenriq.resume_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<StatusDTO> changeUserAccessToken(@RequestBody AccountDTO accountToAdjust) {
        boolean valueReplaced = accSvc.updateUserToken(accountToAdjust);
        if(!valueReplaced) {
            throw new NotFoundException(HttpServletResponse.SC_NOT_FOUND, "Error updating access code.");
        } else {
            return ResponseEntity.ok().body(new StatusDTO(true, HttpServletResponse.SC_OK, "Access code changed."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserViaUuid(@PathVariable String id) {
        UserDTO toReturn = userSvc.getUserFromId(id);
        if(toReturn == null) {
            throw new NotFoundException(HttpServletResponse.SC_NOT_FOUND, "Given user does not exist.");
        } else {
            return ResponseEntity.ok().body(toReturn);
        }
    }

    @GetMapping("/email/{relEmail}")
    public ResponseEntity<UserDTO> getUserViaEmail(@PathVariable("relEmail") String email) {
        UserDTO toReturn = userSvc.getUserFromEmail(email);
        if(toReturn == null) {
            throw new NotFoundException(HttpServletResponse.SC_NOT_FOUND, "Given user does not exist.");
        } else {
            return ResponseEntity.ok().body(toReturn);
        }
    }
}
