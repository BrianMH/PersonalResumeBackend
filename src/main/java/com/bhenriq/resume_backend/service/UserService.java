package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.dto.RoleDTO;
import com.bhenriq.resume_backend.dto.UserDTO;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Exposes basic management methods for the user data object
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper converter;

    public UserDTO getUserFromId(String id) {
        // first see if we can load the relevant user
        Optional<User> relUser = userRepo.findById(id);

        // and then convert it to a relevant DTO type for return
        return relUser.map(user -> converter.map(user, UserDTO.class)).orElse(null);
    }

    public UserDTO getUserFromEmail(String email) {
        // find the user
        Optional<User> relUser = userRepo.findUserByEmail(email);

        // and convert it
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
}
