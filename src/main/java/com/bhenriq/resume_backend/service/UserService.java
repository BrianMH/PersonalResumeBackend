package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Exposes basic management methods for the user data object
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper converter;

    /**
     * Returns the associated user from a given access token. Returns null, otherwise.
     * @param accessToken the access token to request the user from
     * @return a user with associated values
     */
    public User getBaseUserFromAccessToken(String accessToken) {
        Optional<User> retVal = userRepo.findUserByAccessToken(accessToken);
        return retVal.orElse(null);
    }
}
