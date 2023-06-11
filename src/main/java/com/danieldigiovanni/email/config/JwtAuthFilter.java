package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.auth.CustomerDetails;
import com.danieldigiovanni.email.auth.CustomerDetailsService;
import com.danieldigiovanni.email.constants.AuthConstants;
import com.danieldigiovanni.email.utils.JwtUtils;
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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomerDetailsService customerDetailsService;
    private final String TOKEN_SECRET_KEY;

    @Autowired
    public JwtAuthFilter(CustomerDetailsService customerDetailsService, @Value("${token-secret-key}") String tokenSecretKey) {
        this.customerDetailsService = customerDetailsService;
        this.TOKEN_SECRET_KEY = tokenSecretKey;
    }

    @Override
    public void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (
            List.of(AuthConstants.WHITELISTED_ROUTES)
                .contains(request.getServletPath())
        ) {
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
            claims = JwtUtils.extractClaimsFromToken(
                jwt,
                this.TOKEN_SECRET_KEY
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

    private void sendErrorInvalidRequest(HttpServletResponse response, String errorDescription) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.addHeader(
            "WWW-Authenticate",
            "Bearer error=\"invalid_request\", " +
                "error_description=\"" + errorDescription + "\""
        );
    }

    private void sendErrorInvalidToken(HttpServletResponse response, String errorDescription) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.addHeader(
            "WWW-Authenticate",
            "Bearer error=\"invalid_token\", " +
                "error_description=\"" + errorDescription + "\""
        );
    }

}
