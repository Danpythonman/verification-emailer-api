package com.danieldigiovanni.email.config.filter;

import com.danieldigiovanni.email.auth.CustomerDetails;
import com.danieldigiovanni.email.auth.CustomerDetailsService;
import com.danieldigiovanni.email.auth.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter to authorize requests based on the JWT provided in the Authorization
 * header.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomerDetailsService customerDetailsService;
    private final JwtUtils jwtUtils;
    private final List<String> whitelistedRoutes;
    private final String tokenSecretKey;

    @Autowired
    public JwtAuthFilter(
        CustomerDetailsService customerDetailsService,
        JwtUtils jwtUtils,
        @Qualifier("whitelistedRoutes") List<String> whitelistedRoutes,
        @Value("${token-secret-key}") String tokenSecretKey
    ) {
        this.customerDetailsService = customerDetailsService;
        this.jwtUtils = jwtUtils;
        this.whitelistedRoutes = whitelistedRoutes;
        this.tokenSecretKey = tokenSecretKey;
    }

    /**
     * Filters requests based on the provided JWT in the Authorization header.
     * <p>
     * The following checks must pass for a request to pass this filter:
     * <ul>
     *     <li>Authorization header must be present.</li>
     *     <li>Authorization header must be using Bearer authentication.</li>
     *     <li>Authorization token must be a valid JWT.</li>
     *     <li>JWT must not be expired.</li>
     *     <li>JWT pass signature validation.</li>
     *     <li>JWT subject must be a valid id of an existing customer.</li>
     * </ul>
     * If any of the above checks fail, the filter chain is stopped and a
     * response is returned with an HTTP error status code and a
     * WWW-Authenticate header.
     * <p>
     * Register and login routes are whitelisted, and no authorization is
     * required to access them.
     *
     * @param request     HTTP request. Must include the Authorization header.
     * @param response    HTTP response. Will be populated and sent back in the
     *                    event of any of the above validation failures.
     * @param filterChain The filter chain. Will be continued if authorization
     *                    succeeds.
     *
     * @throws ServletException If an I/O error occurs during the processing of
     *                          the filter chain.
     * @throws IOException      If the processing of the filter chain fails for
     *                          any other reason.
     */
    @Override
    public void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (this.whitelistedRoutes.contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            this.sendErrorInvalidRequest(
                response,
                "Access token was not provided"
            );
            return;
        }

        Claims claims;
        final String jwt = authHeader.substring(7);
        try {
            claims = this.jwtUtils.extractClaimsFromToken(
                jwt,
                this.tokenSecretKey
            );
        } catch (
            IllegalArgumentException
            | MalformedJwtException
            | UnsupportedJwtException exception
        ) {
            this.sendErrorInvalidToken(response, "Access token is invalid");
            return;
        } catch (ExpiredJwtException exception) {
            this.sendErrorInvalidToken(response, "Access token is expired");
            return;
        } catch (SignatureException exception) {
            this.sendErrorInvalidToken(
                response,
                "Access token signature is invalid"
            );
            return;
        }

        String subject = claims.getSubject();
        if (subject == null) {
            this.sendErrorInvalidToken(
                response,
                "Access token is missing subject"
            );
            return;
        }

        CustomerDetails customerDetails;
        try {
            customerDetails
                = this.customerDetailsService.loadUserByUsername(subject);
        } catch (NumberFormatException exception) {
            this.sendErrorInvalidToken(
                response,
                "Access token subject is invalid"
            );
            return;
        } catch (UsernameNotFoundException exception) {
            this.sendErrorInvalidToken(
                response,
                "Access token subject does not exist"
            );
            return;
        }

        UsernamePasswordAuthenticationToken authToken
            = new UsernamePasswordAuthenticationToken(
            customerDetails,
            null,
            customerDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    /**
     * Sets the status code of the response to 400 (bad request) and sets the
     * WWW-Authenticate header's <code>error</code> field to "invalid_request"
     * and <code>error_description</code> field to the given error description.
     *
     * @param response         The response to be given the status code and
     *                         WWW-Authenticate header.
     * @param errorDescription The error description to be used for the
     *                         <code>error_description</code> field of the
     *                         WWW-Authenticate header.
     */
    private void sendErrorInvalidRequest(HttpServletResponse response, String errorDescription) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.addHeader(
            "WWW-Authenticate",
            "Bearer error=\"invalid_request\", " +
                "error_description=\"" + errorDescription + "\""
        );
    }

    /**
     * Sets the status code of the response to 401 (unauthorized) and sets the
     * WWW-Authenticate header's <code>error</code> field to "invalid_token"
     * and <code>error_description</code> field to the given error description.
     *
     * @param response         The response to be given the status code and
     *                         WWW-Authenticate header.
     * @param errorDescription The error description to be used for the
     *                         <code>error_description</code> field of the
     *                         WWW-Authenticate header.
     */
    private void sendErrorInvalidToken(HttpServletResponse response, String errorDescription) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.addHeader(
            "WWW-Authenticate",
            "Bearer error=\"invalid_token\", " +
                "error_description=\"" + errorDescription + "\""
        );
    }

}
