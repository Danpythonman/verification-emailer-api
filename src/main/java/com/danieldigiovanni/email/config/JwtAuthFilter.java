package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.auth.CustomerDetails;
import com.danieldigiovanni.email.auth.CustomerDetailsService;
import com.danieldigiovanni.email.utils.JwtUtils;
import io.jsonwebtoken.Claims;
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
import java.util.Date;

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
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_request\", error_description=\"Access token was not provided\"");
            return;
        }

        final String jwt = authHeader.substring(7);

        Claims claims = JwtUtils.extractClaimsFromToken(
            jwt, this.TOKEN_SECRET_KEY
        );

        Date expiration = claims.getExpiration();
        if (new Date().after(expiration)) {
            filterChain.doFilter(request, response);
            return;
        }

        String subject = claims.getSubject();
        if (subject == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Access token is missing subject\"");
            return;
        }

        CustomerDetails customerDetails;
        try {
            customerDetails
                = this.customerDetailsService.loadUserByUsername(subject);
        } catch (NumberFormatException exception) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Access token subject is invalid\"");
            return;
        } catch (UsernameNotFoundException exception) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Access token subject does not exist\"");
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

}
