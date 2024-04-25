package com.bhenriq.resume_backend.config;

import com.bhenriq.resume_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
@Configuration
public class SecurityTokenConfig {
    @Autowired
    private UserService userSvc;

    /**
     * Since all authentication happens via access tokens, we don't need the user details service
     * (as all authentication/authorization happens in our AccessTokenFilter)
     * @return Dummy UserDetailsService
     */
    @Bean
    public UserDetailsService emptyDetailsService() {
        return username -> { throw new UsernameNotFoundException("Only access tokens are allowed."); };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // handle an authorized attempts
                .exceptionHandling(exception -> exception.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                // Add a filter to validate the tokens with every request
                .addFilterBefore(new AccessTokenFilter(userSvc), UsernamePasswordAuthenticationFilter.class)
                // authorization requests config
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/applications/**").hasAuthority("ADMIN")
                    .requestMatchers("/api").permitAll()
                )
                .build();
    }
}