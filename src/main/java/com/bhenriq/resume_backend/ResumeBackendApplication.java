package com.bhenriq.resume_backend;

import com.bhenriq.resume_backend.config.AccessTokenFilter;
import com.bhenriq.resume_backend.model.Account;
import com.bhenriq.resume_backend.model.Role;
import com.bhenriq.resume_backend.model.User;
import com.bhenriq.resume_backend.repository.AccountRepository;
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
    @Autowired
    private AccountRepository accountRepo;

    @Value("${BACKEND_API_KEY}")
    private String DEFAULT_API_KEY;

    /**
     * Sets up our pre-defined roles (without the ROLE_ prefix as they are reported as authorities)
     */
    private void setupRoles() {
        List<Role> relevantRoles = List.of(
                new Role("USER_ACCOUNT_ADMIN"), // READ + MODIFY USER + ACCOUNTS
                new Role("APPLICATION_ADMIN"), // READ + MODIFY APPLICATION
                new Role("BLOG_ADMIN"), // READ + MODIFY BLOG POSTS
                new Role("GENERAL_ADMIN")   // READ+MODIFY FOR ALL ELEMENTS
        );

        // and then we can save all of these values to the database
        roleRepo.saveAll(relevantRoles);
    }

    /**
     * Sets up the base user along with the relevant pre-specified access key
     */
    private void setupAPIUser() {
        // first create the user for the API User
        User apiUser = new User("a@a.com", "API_USER");
        apiUser.addAuthority(roleRepo.findById("USER_ACCOUNT_ADMIN").orElse(null));

        // and then add an API access account
        Account apiAccount = new Account(UUID.randomUUID().toString(),
                AccessTokenFilter.API_KEY_TYPE,
                AccessTokenFilter.API_PROVIDER,
                null,
                "123456789",
                null,
                apiUser);

        // and save the given user and account
        userRepo.save(apiUser);
        accountRepo.save(apiAccount);
    }

    @Override
    public void run(String... args) {
        setupRoles();
        setupAPIUser();
    }


    public static void main(String[] args) {
        SpringApplication.run(ResumeBackendApplication.class, args);
    }

}
