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

import java.time.LocalDate;
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
    @Autowired
    private ResumeProjectRepository resProjRepo;
    @Autowired
    private ReferenceRepository refRepo;

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
                new Pair<>("AWS", 80)
        );
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
        List<String> researchBullets = List.of(
                "Designed control scheme and software GUI interface for snake-like colonoscopy robot prototype using Python and MATLAB",
                "Tripled robot sensor polling rate by improving on the ARM C-based implementation",
                "Assisted in data collection of robot performance, wrote journal paper drafts, and presented on an MVP publicly"
        );
        List<String> tutorBullets = List.of(
                "Increased student course enrollment by re-structuring the face recognition project to expand on theoretical underpinnings and modifying it to use up-to-date APIs",
                "Mentored several student software development teams simultaneously and functioned as a domain expert and scrum leader, ensuring deadlines were met and goals were feasible",
                "Created student resources & documentation for multiple in-development projects to slowly introduce to the course",
                "Researched and implemented minimum viable products for prospective course project ideas in a team of four"
        );
        List<String> taBullets = List.of(
                "Engaged in one-on-ones with students during lab sections and office hours sections to further knowledge of common circuit systems and their issues",
                "Increased student course satisfaction by holding discussion sections that involved difficult-to-solve circuit systems and a more interactive environment"
        );

        List<Reference> tutorRefs =  refRepo.saveAll(List.of(
                new Reference(null, "https://pib.ucsd.edu/", Reference.IconTypes.WEB_ICON.toString(), "Course Site"),
                new Reference(null, "https://sites.google.com/view/ece-196/face-recognition", Reference.IconTypes.BOOK_ICON.toString(), "Project Page"),
                new Reference(null, "https://github.com/ProjectInABox", Reference.IconTypes.GITHUB_ICON.toString(), "Course Github")
        ));

        List<ResumeExperience> relevantExps = List.of(
                new ResumeExperience(
                        null,
                        "Research",
                        "Student Research Intern",
                        "University of California - San Diego",
                        LocalDate.of(2018, 8, 1),
                        LocalDate.of(2018, 9, 1),
                        researchBullets,
                        null
                ),
                new ResumeExperience(
                        null,
                        "Contract",
                        "Makerspace Tutor",
                        "University of California - San Diego",
                        LocalDate.of(2017, 9, 1),
                        LocalDate.of(2019, 9, 1),
                        tutorBullets,
                        tutorRefs
                ),
                new ResumeExperience(
                        null,
                        "Contract",
                        "Teaching Assistant",
                        "University of California - San Diego",
                        LocalDate.of(2019, 7, 1),
                        LocalDate.of(2019, 9, 1),
                        taBullets,
                        null
                )
        );

        resExpRepo.saveAll(relevantExps);
    }

    private void setupResumeEducation() {
        Set<String> psTopics = Set.of("Java", "Spring Framework", "Web Development", "Typescript", "Next.js", "React.js", "REST", "Microservices", "HTML/CSS");
        Set<String> msTopics = Set.of("Data Analysis", "Deep Learning", "Generative Models", "Computer Vision", "Control Systems", "Python", "Data Mining", "Hadoop", "Spark", "Linear Algebra", "Git", "PCB Design", "Embedded Programming");
        Set<String> bsTopics = Set.of("ARM C++", "C++", "Rapid Prototyping", "Python", "Machine Learning", "Statistical Learning", "Data Structures", "Algorithms", "Data Analysis", "Java", "Git", "Project Management");
        List<ResumeEducation> relevantEds = List.of(
                new ResumeEducation(null, "Per Scholas", "Further Education",
                        "Full Stack Java Development", null, null,
                        LocalDate.of(2023, 12, 1), LocalDate.of(2024, 3, 1), psTopics),
                new ResumeEducation(null, "University of California - San Diego (UCSD)", "MS",
                        "Electrical Engineering (Machine Learning & Data Science", 3.34, "Machine Learning & Data Science",
                        LocalDate.of(2018, 9, 1), LocalDate.of(2020, 6, 12), msTopics),
                new ResumeEducation(null, "University of California - San Diego (UCSD)", "BS",
                        "Electrical Engineering", 3.73, "Machine Learning",
                        LocalDate.of(2014, 9, 1), LocalDate.of(2018, 6, 12), bsTopics)
        );

        resEduRepo.saveAll(relevantEds);
    }

    // Unlike the other values, there's just too many of this kind to set up. Instead, I will set up one of them and the
    // rest will be added manually.
    private void setupResumeProjects() {
        List<String> projBullets = List.of(
                "Created a dynamically updatable front-end server with proper resource caching and hosted it on Vercel",
                "Designed a database for the front-end and implemented it using PostgreSQL along with an interface in Java using Spring Framework",
                "Hosted the back-end on AWS RDS + AWS EC2 and created a static resource bucket on S3 with caching on the edge",
                "Implemented an Auth.js-based middleware layer to secure front-end and complemented it with a Spring JWT role-based authentication scheme on the back-end",
                "Mapped proper DNS entries on Namespace and created proper SSL certification to prevent data leakage and CORS errors between the front-end and back-end."
        );
        String name = "AWS/Vercel Hosted Resume Website";
        String description = "A resume-based site using a Next.js front-end server on Vercel and a Spring back-end server on AWS.";

        ResumeProject toAdd = new ResumeProject(
                null,
                name,
                description,
                "Full Stack Developer",
                "Personal Project",
                LocalDate.of(2024, 3, 1),
                null,
                projBullets,
                null
        );

        resProjRepo.save(toAdd);
    }

    @Override
    public void run(String... args) {
//        setupRoles();
//        setupAPIUser();
//        setupBaseAdminUserPrivileges();
//        setupInitialBlogPost();
//        setupResumeSkills();
//        setupResumeExperience();
//        setupResumeEducation();
//        setupResumeProjects();
    }


    public static void main(String[] args) {
        SpringApplication.run(ResumeBackendApplication.class, args);
    }

}
