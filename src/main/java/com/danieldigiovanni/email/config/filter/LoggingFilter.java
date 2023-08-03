package com.danieldigiovanni.email.config.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to log information about each request.
 */
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    /**
     * Logs the HTTP method and URL path of each request.
     *
     * @param request     HTTP request.
     * @param response    HTTP response.
     * @param filterChain The filter chain. Should always continue.
     *
     * @throws ServletException If an I/O error occurs during the processing of
     *                          the filter chain.
     * @throws IOException      If the processing of the filter chain fails for
     *                          any other reason.
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        this.log.info(
            "Handling {} request to {}",
            request.getMethod(),
            request.getRequestURI()
        );
        filterChain.doFilter(request, response);
    }

}
