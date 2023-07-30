package com.danieldigiovanni.email.config.filter;

import com.danieldigiovanni.email.metrics.Metrics;
import com.danieldigiovanni.email.metrics.MetricsService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * Filter to track metrics for requests.
 */
@Component
public class MetricsFilter extends OncePerRequestFilter {

    private final MetricsService metricsService;
    private final List<String> whitelistedRoutes;
    private final Logger log = LoggerFactory.getLogger(MetricsFilter.class);

    public MetricsFilter(
        MetricsService metricsService,
        @Qualifier("whitelistedRoutes") List<String> whitelistedRoutes
    ) {
        this.metricsService = metricsService;
        this.whitelistedRoutes = whitelistedRoutes;
    }

    /**
     * Save metrics for each request that requires authorization.
     * <p>
     * Requests that don't require authorization (like login and register) get
     * bypassed.
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
        if (this.whitelistedRoutes.contains(request.getServletPath())) {
            // No metrics tracked for routes that don't require authorization
            filterChain.doFilter(request, response);
            return;
        }

        Principal principal = SecurityContextHolder.getContext().getAuthentication();

        if (principal == null) {
            this.log.warn(
                "Principal is null, so no metrics are tracked. " +
                    "Continuing filter chain anyway."
            );
            filterChain.doFilter(request, response);
            return;
        }

        Long customerId = Long.valueOf(principal.getName());

        Metrics metrics = Metrics.builder()
            .setCustomerId(customerId)
            .setRequestDate(new Date())
            .setRequestMethod(request.getMethod())
            .setRequestURI(request.getRequestURI())
            .build();

        this.metricsService.trackMetrics(metrics);

        filterChain.doFilter(request, response);
    }

}
