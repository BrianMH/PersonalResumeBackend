package com.bhenriq.resume_backend.config;

import com.bhenriq.resume_backend.service.AccountService;
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
import java.util.UUID;

/**
 * A security filter that can provide authentication and authorization given a request with an HTTP header carrying
 * a bearer access token. For this access token to work, the filter expects a request header with the following parameters:
 *
 * HEADER {
 *      ...,
 *      Authorization: Bearer {ACCESS_TOKEN}
 *      Referer: https://{DOMAIN_ROOT}/auth/{PROVIDER}
 *      ...,
 * }
 *
 * The body is not manipulated and passed to the requested controller following authentication. If no referer is
 * passed, then the default provider is presumed to be intrinsic to the system (so of form API_PROVIDER).
 */
@AllArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {
    private static final int BEARER_OFFSET = 7;
    public static final String API_KEY_TYPE = "root_key";
    public static final String API_PROVIDER = "system";

    private AccountService accSvc;

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
        String referer = req.getHeader("Referer");

        // make sure we are evaluating for the right user
        UserDetails givenUser;
        if(referer != null && !referer.isEmpty()) {
            logger.debug("Request container referer header value - " + referer);

            // and now we attempt retrieval
            givenUser = accSvc.getBaseUserFromAccessTokenAndProvider(token, referer);
        } else {
            // otherwise try loading assuming given API key is database sourced
            givenUser = accSvc.getBaseUserFromAccessTokenAndProvider(token, API_PROVIDER);
        }

        if(givenUser == null) {
            logger.debug(String.format("Could not find given access key within database - %s", token));
            nextFilter.doFilter(req, res);
            return;
        }
        logger.debug(String.format("Found user with given access token - %s", givenUser.getUsername()));

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