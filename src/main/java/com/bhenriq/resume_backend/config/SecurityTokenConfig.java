package com.bhenriq.resume_backend.config;

import com.bhenriq.resume_backend.service.AccountService;
import com.bhenriq.resume_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Security;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
@Configuration
public class SecurityTokenConfig {
    @Autowired
    private AccountService accSvc;

    /**
     * Since all authentication happens via access tokens, we don't need the user details service
     * (as all authentication/authorization happens in our AccessTokenFilter)
     * @return Dummy UserDetailsService
     */
    @Bean
    public UserDetailsService emptyDetailsService() {
        return username -> { throw new UsernameNotFoundException("Only access tokens are allowed."); };
    }

    /**
     * Sets up the general gist of our base config along with the security matcher
     */
    public HttpSecurity tokenSecurityBaseConfig(@NonNull HttpSecurity http, @NonNull String pathToSecure) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // first only match given path
                .securityMatcher(pathToSecure)
                // and turn off session management as this is a token-based REST API
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // make sure errors send back a 401 if an exception is found later in the chain
                .exceptionHandling(exception -> exception.authenticationEntryPoint(((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))))
                // and then add our access token filter before the dummy username auth filter
                .addFilterBefore(new AccessTokenFilter(accSvc), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Order(2)
    public SecurityFilterChain usersFilterChain(HttpSecurity http) throws Exception {
        return tokenSecurityBaseConfig(http, "/api/users/**")
                .authorizeHttpRequests(auth -> auth
                        // sub-admin roles have unrestricted access to specific controllers and only admins for now
                        .requestMatchers("/api/users/**").hasAnyAuthority("USER_ACCOUNT_ADMIN", "GENERAL_ADMIN")
                )
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain blogFilterChain(HttpSecurity http) throws Exception {
        return tokenSecurityBaseConfig(http, "/api/blog/**")
                .authorizeHttpRequests(auth -> auth
                        // first allow users to access specific paths which anyone can access
                        .requestMatchers("/api/blog/posts/{id}").permitAll()
                        .requestMatchers("/api/blog/posts/{id}/preview").permitAll()
                        .requestMatchers("/api/blog/tags/all").permitAll()
                        .requestMatchers("/api/blog/posts/paged/**").permitAll()

                        // and lock any other request behind the necessary admin privileges
                        .anyRequest().hasAnyAuthority("BLOG_ADMIN", "GENERAL_ADMIN")
                )
                .build();
    }

    /**
     * ALlows the health portion of the application to be accessible by any user.
     * This should run first as is it is the most specific as can be.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain healthApiFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api").permitAll()
                )
                .build();
    }

    /**
     * By default, we choose to not allow any users access to any page outside of those that we choose to allow.
     *
     * Order(999) is simply required to make sure it's the last filter to run.
     */
    @Bean
    @Order(9999)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        return tokenSecurityBaseConfig(http, "**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasAuthority("GENERAL_ADMIN")
                )
                .build();
    }
}