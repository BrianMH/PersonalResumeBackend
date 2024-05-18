package com.bhenriq.resume_backend.dummy;

import java.util.List;
import java.util.Set;

/**
 * Contains some consts that will be used for the initial post. This will eventually get pushed off to a file for a
 * reader to use instead, but for now this should suffice.
 */
public class InitialPost {
    public static final String HEADER_IM = "servers.png";
    public static final String POST_TITLE = "First (Server) Post";
    public static final String POST_CONTENT =
            """
                        <section>
                        <p>
                            This is a statically uploaded blog post regarding the progress of development on the site. First thing's first, the
                            given post is technically just templated HTML, which can have a few disadvantages when compared to fully planned
                             out pages the way we would normally design them in Next.JS. In particular, we lose:
                        </p>
                        
                        <ul>
                            <li>Convenient pre-defined css classes via Tailwind/Bootstrap without incorporating further external css files.</li>
                            <li>Image serving optimizations provided by Next.JS when using the built-in <code>&lt;Image\\&gt;</code> tag</li>
                            <li>No simplicity of design via WYSIWYG editing (Namely, we have to design with the site in mind without live previews)</li>
                        </ul>
                        
                        <p>
                            But not all is lost! In fact, we can work around this by keeping these negatives in mind while designing the page.
                            We would want all of our elements to pretty much conform to the typical design associated with the blog and only
                            further complicate via in-line styles if completely necessary. While this may seem like an issue, there really isn't
                            much reason to break the flow of legibility in a page with in-line images or anything of that sort (at least for now).
                            Collections of images may require some wrangling with divs and flex or grid layouts, but it wouldn't come by frequently
                            enough to be an issue.
                        </p>
                    </section>
                        
                    <section>
                        <h3>Frontend Progress </h3>
                        
                        <p>
                            So with that stated, we can start with the general overview of the progress that has been made in the site. In particular,
                            we can see that the <a href="/work">Experience</a> page and the <a href="/">About</a> pages have been properly implemented.
                            However, of note is that they are currently just static pages. The backend is planned to be hosted on AWS
                            and served using Java's Spring Framework on EC2 via a RESTful API. There are other factors that are planned to become in play in the backend,
                            but I will keep it simple for now as this particular section is more a description of what's going on with the front-end.
                        </p>
                        
                        <p>
                            The design of the page seems to work decently well, but there potentially might be an issue with text coloring once
                            it gets swapped to dark mode. I plan to revisit any off-looking components once it's set in stone, but that will likely
                            be one of the last tasks to deal with as it's not necessary for base site functionality. With design in-mind, the blog
                            may actually still change in appearance to something a bit more friendly for a technical blog, but for now the current
                            design will suffice with presenting header image previews along with the post title.
                        </p>
                        
                        <p>
                            Since the blog is mostly second to the purpose of the site, it will likely take more time to receive updates. 
                            For example, since the tag selection will not be relevant until the backend is completely connected, the current
                            skeleton gives a sufficient idea about how things will work out when connected with the backend. The ony thing
                            that would be relevant to add onto the blog would be a table of contents on the left-hand side post view layout so that users can navigate to a
                            specific section of the blog easily. DigitalOcean has a pretty nice looking blog with a gauge on the top to roughly indicate
                            the amount of page left, but it seems unnecessary to try and replicate that completely. One problem that comes to
                            mind would be management of section tag names, as this would likely need to be scraped off the content post and
                            then cached on the server side.
                        </p>
                        
                    </section>
                        
                    <section>
                        <h3>Backend Progress</h3>
                        
                        <p>
                            Currently, the backend only has some object types defined along with their corresponding 1-1 DAOs and service / 
                            repository layer entities established. For all functions I would need to define a REST based request for any
                            type of data that I might need. While creating the REST API isn't a huge issue, it can be a huge pain having
                            to redefine and manage DAO entities manually. Upon investigating, I did come across GraphQL which seems to address
                            part of the problem, but having to deal with an entirely new API seems a bit overkill for a simple project like this.
                            More investigation will be needed for the serving part of the backend.
                        </p>
                        
                        <p>
                            Furthermore, some investigation was placed into packaging the entire Spring server into a Docker container, which 
                            would be automatically uploaded to AWS and then used to launch an updated instance of the server of EC2. The later
                            half has yet to be realized (as it requires a bit more messing with scripting and startup tasks), but that will
                            likely take place after actual connectivity is finished. The purpose of such an action is largely in the spirit of
                            CI/CD, but it's also just in case I happen to forget to re-launch the backend following an update to the backend repository.
                        </p>
                        
                        <figure>
                            <img src="ResumeServerArchitecture-1.png" alt="Current ideal architecture of the site" />
                            
                            <figcaption>
                                <p>Figure 1</p>
                                <p>
                                    The planned site architecture. Thick black lines indicate the main flow of
                                    requests while the red lines indicate event triggers. The dotted black line is simply an indicator of API
                                    access being limited to the EC2 instance.
                                </p>
                            </figcaption>
                        </figure>
                        
                        <p>
                            And now for the general model of the back-end along with the (yet-to-be actualized) front-end connection on Vercel.
                            Data remains relative up until it hits the frontend, at which point the frontend combines the known CDN with the postID and
                            the given image name such that the final path is then converted to <code>CLOUDFRONT_ORIGIN/dynamic/POST_ID/IMAGE_NAME</code>
                            and properly rendered on the client's end without revealing too much about the backing S3 bucket. It also makes it a bit less
                            easy to accidentally mess up with image filenames as they only need to be kept unique with respect to the given post.
                        </p>
                        
                        <p>
                            Cloudfront also seems to be putting in a bunch of work as a CDN since it simplified serving directly from the S3 bucket (and serving
                            cached content is generally cheaper than serving fresh content directly from the bucket anyway). In either case, the one
                            caveat is that performing page updates can become quite complicated without applying some sort of versioning (as 
                            invalidations cost money...). This will require a bit of round-about logic in the server end, but it shouldn't be too big of
                            an issues as updates won't generally come frequently enough to run out of image names.
                        </p>
                    </section>
                        
                    <section>
                        <h3>Looking Ahead...</h3>
                        
                        <p>
                            Despite the fair amount of progress being made, it does feel like there's just as much to work on as when this project
                            started. Sure, layouts now seem fine, but several questions arose as a result: 
                        </p>
                        
                        <ol>
                            <li>How will the admin dashboard look and feel like? </li>       
                            <li>How are blog posts to be created in a way that can allow for live previewing of the HTML data?</li>
                            <li>How can certain elements be simplified to prevent redundancy on the backend?</li>
                        </ol>
                        
                        <p>
                            Despite all of these questions arising as a result of the progress being made, I am confident that each one of them
                            will be resolved in a satisfactory manner. Since I'm not the most familiar with web development practices (nor with
                            general dev ops activities in the backend/frontend), all of these questions will deserve their own fair share of
                            investigation.
                        </p>
                    </section>
            """;
    public static final Set<String> POST_IMAGES = Set.of("ResumeServerArchitecture-1.png",
                                                         "servers.png");
}
