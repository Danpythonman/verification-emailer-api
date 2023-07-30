package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.config.filter.JwtAuthFilter;
import com.danieldigiovanni.email.config.filter.LoggingFilter;
import com.danieldigiovanni.email.config.filter.MetricsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final LoggingFilter loggingFilter;
    private final MetricsFilter metricsFilter;
    private final String[] whitelistedRoutes;

    @Autowired
    public SecurityConfig(
        AuthenticationProvider authenticationProvider,
        JwtAuthFilter jwtAuthenticationFilter,
        LoggingFilter loggingFilter,
        MetricsFilter metricsFilter,
        @Qualifier("whitelistedRoutes") List<String> whitelistedRoutes
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthenticationFilter;
        this.loggingFilter = loggingFilter;
        this.metricsFilter = metricsFilter;
        this.whitelistedRoutes = whitelistedRoutes.toArray(new String[0]);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(this.whitelistedRoutes).permitAll()
                .requestMatchers("/error").anonymous()
                .anyRequest().authenticated())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(this.authenticationProvider)
            .addFilterBefore(
                this.jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(
                this.loggingFilter,
                JwtAuthFilter.class)
            .addFilterAfter(
                this.metricsFilter,
                JwtAuthFilter.class)
            .build();
    }

}
