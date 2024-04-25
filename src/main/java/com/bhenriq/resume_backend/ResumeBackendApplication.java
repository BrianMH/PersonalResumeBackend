package com.bhenriq.resume_backend;

import com.bhenriq.resume_backend.model.Role;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.RoleRepository;
import com.bhenriq.resume_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

@SpringBootApplication
public class ResumeBackendApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Value("${BACKEND_API_KEY}")
    private String DEFAULT_API_KEY;
    @Value("${ADMIN_USER_EMAIL}")
    private String ADMIN_EMAIL;

    /**
     * Sets up our pre-defined roles (without the ROLE_ prefix as they are reported as authorities)
     */
    private void setupRoles() {
        List<Role> relevantRoles = List.of(
                new Role("TOKEN_ADMIN"), // OP_UPDATE_TOKEN + OP_UPDATE_TOKEN_EXPIRY
                new Role("USER_ADMIN"), // READ + MODIFY USER
                new Role("APPLICATION_ADMIN") // READ + MODIFY APPLICATION
        );

        // and then we can save all of these values to the database
        roleRepo.saveAll(relevantRoles);
    }

    /**
     * Sets up the base user along with the relevant pre-specified access key
     */
    private void setupAPIUser() {
        User apiUser = new User(null, "API_USER", DEFAULT_API_KEY, null);
        apiUser.addAuthority(roleRepo.findById("TOKEN_ADMIN").orElse(null));
        userRepo.save(apiUser);
    }

    /**
     * This sets up the admin role along with some pre-specified inputs
     */
    private void setupAdminUser() {
        User adminUser = new User(ADMIN_EMAIL, "ADMIN", null, null);
        adminUser.addAuthority(roleRepo.findById("USER_ADMIN").orElse(null));
        adminUser.addAuthority(roleRepo.findById("APPLICATION_ADMIN").orElse(null));
        userRepo.save(adminUser);
    }

    @Override
    public void run(String... args) {
        setupRoles();
        setupAPIUser();
        setupAdminUser();
    }


    public static void main(String[] args) {
        SpringApplication.run(ResumeBackendApplication.class, args);
    }

}
