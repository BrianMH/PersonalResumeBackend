package com.bhenriq.resume_backend;

import com.bhenriq.resume_backend.config.AccessTokenFilter;
import com.bhenriq.resume_backend.dummy.InitialPost;
import com.bhenriq.resume_backend.model.*;
import com.bhenriq.resume_backend.repository.*;
import com.bhenriq.resume_backend.service.S3BucketService;
import com.bhenriq.resume_backend.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

/**
 * The entry point of the application.
 *
 * TODO: Move initializations outside of this section to their own appropriate utility initializer based on sections.
 */
@SpringBootApplication
public class ResumeBackendApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private EmailWhitelistRepository emailListRepo;
    @Autowired
    private BlogPostRepository blogPostRepo;
    @Autowired
    private BlogPostTagRepository blogPostTagRepo;
    @Autowired
    private ResumeSkillRepository resSkillRepo;
    @Autowired
    private ResumeEducationRepository resEduRepo;
    @Autowired
    private ResumeExperienceRepository resExpRepo;

    @Value("${BACKEND_API_KEY}")
    private String DEFAULT_API_KEY;
    @Value("${ADMIN_USER_EMAIL}")
    private String DEFAULT_ADMIN_EMAIL;

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
        User apiUser = new User(null, "API_USER");
        apiUser.addAuthority(roleRepo.findById("USER_ACCOUNT_ADMIN").orElse(null));

        // and then add an API access account
        Account apiAccount = new Account(UUID.randomUUID().toString(),
                AccessTokenFilter.API_KEY_TYPE,
                AccessTokenFilter.API_PROVIDER,
                null,
                DEFAULT_API_KEY,
                null,
                apiUser);

        // and save the given user and account
        userRepo.save(apiUser);
        accountRepo.save(apiAccount);
    }

    private void setupBaseAdminUserPrivileges() {
        // since user creation is managed by the front-end, we instead indirectly associate roles through the email whitelist
        // used during creation
        EmailWhitelist adminWhitelistObj = new EmailWhitelist(DEFAULT_ADMIN_EMAIL, Set.of(
                roleRepo.findById("GENERAL_ADMIN").orElseThrow(() -> {return new RuntimeException("Encountered missing role value");})
        ));

        emailListRepo.save(adminWhitelistObj);
    }

    private void setupInitialBlogPost() {
        // Set up our initial tags first as they will constantly be reused and save them
        BlogPostTag backendTag = new BlogPostTag(null, "Back-end", null);
        BlogPostTag frontendTag = new BlogPostTag(null, "Front-end", null);
        BlogPostTag updateTag = new BlogPostTag(null, "Updates", null);
        List<BlogPostTag> relTags = blogPostTagRepo.saveAll(List.of(backendTag, frontendTag, updateTag));
        Set<BlogPostTag> savedTags = new HashSet<>(relTags);

        // This is essentially the first post. We can manually set it up here.
        BlogPost firstPost = new BlogPost(null, savedTags, InitialPost.HEADER_IM, InitialPost.POST_TITLE, InitialPost.POST_CONTENT, InitialPost.POST_IMAGES, false, null, null);
        blogPostRepo.save(firstPost);
    }

    private void setupResumeSkills() {
        List<Pair<String, Integer>> technicalSkills = List.of(
                new Pair<>("Python", 90),
                new Pair<>("Java", 85),
                new Pair<>("Deep Learning", 85),
                new Pair<>("Data Analysis", 80),
                new Pair<>("Typescript", 80),
                new Pair<>("Next.js", 70),
                new Pair<>("Spring Framework", 80),
                new Pair<>("REST API", 80),
                new Pair<>("HTML/CSS", 80),
                new Pair<>("AWS", 80));
        technicalSkills.forEach(skillMap -> { resSkillRepo.save(new ResumeSkill(null, skillMap.left, skillMap.right, "Technical")); });

        List<Pair<String, Integer>> softSkills = List.of(
                new Pair<>("Project Management", 95),
                new Pair<>("Documentation", 85),
                new Pair<>("Communication", 95),
                new Pair<>("Leadership", 85),
                new Pair<>("Adaptability", 90)
        );
        softSkills.forEach(skillMap -> { resSkillRepo.save(new ResumeSkill(null, skillMap.left, skillMap.right, "Soft")); });
    }

    private void setupResumeExperience() {

    }

    private void setupResumeEducation() {

    }

    @Override
    public void run(String... args) {
//        setupRoles();
//        setupAPIUser();
//        setupBaseAdminUserPrivileges();
//        setupInitialBlogPost();
        setupResumeSkills();
        setupResumeExperience();
        setupResumeEducation();
    }


    public static void main(String[] args) {
        SpringApplication.run(ResumeBackendApplication.class, args);
    }

}
