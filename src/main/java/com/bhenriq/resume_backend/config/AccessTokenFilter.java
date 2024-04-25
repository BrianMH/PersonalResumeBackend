package com.bhenriq.resume_backend.config;

import com.bhenriq.resume_backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A security filter that can provide authentication and authorization given a request with an HTTP header carrying
 * a bearer access token
 */
@AllArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {
    private static final int BEARER_OFFSET = 7;

    private UserService userSvc;

    @Override
    public void doFilterInternal(HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain nextFilter)
            throws IOException, ServletException {
        // format "Authorization: Bearer {TOKEN} enforcement
        String token = req.getHeader("Authorization");
        if (token == null || !token.strip().startsWith("Bearer")) {
            logger.debug("Improper/Empty authorization header encountered...");
            nextFilter.doFilter(req, res);
            return;
        }

        // And from here we will need our tokens verified
        token = token.strip().substring(BEARER_OFFSET).strip();
        UserDetails givenUser = userSvc.getBaseUserFromAccessToken(token);
        if(givenUser == null) {
            logger.debug(String.format("Could not find given access key within database - %s:", token));
            nextFilter.doFilter(req, res);
            return;
        }
        logger.debug(String.format("Found request with given access token - %s", token));

        // if account is not valid, then we can pass through this filter
        if(!givenUser.isAccountNonExpired() || !givenUser.isAccountNonLocked() || !givenUser.isCredentialsNonExpired() || !givenUser.isEnabled()) {
            nextFilter.doFilter(req, res);
            return;
        }

        // otherwise continue assigning roles in the security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(givenUser, null, givenUser.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        logger.debug(String.format("Set authorization context given user with known roles - %s", givenUser.getAuthorities()));

        // and then continue in the chain
        nextFilter.doFilter(req, res);
    }
}