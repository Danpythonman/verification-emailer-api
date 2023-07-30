package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.config.filter.JwtAuthFilter;
import com.danieldigiovanni.email.config.filter.LoggingFilter;
import com.danieldigiovanni.email.config.filter.MetricsFilter;
import com.danieldigiovanni.email.constants.AuthConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final LoggingFilter loggingFilter;
    private final MetricsFilter metricsFilter;

    @Autowired
    public SecurityConfig(
        AuthenticationProvider authenticationProvider,
        JwtAuthFilter jwtAuthenticationFilter,
        LoggingFilter loggingFilter,
        MetricsFilter metricsFilter
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthenticationFilter;
        this.loggingFilter = loggingFilter;
        this.metricsFilter = metricsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(AuthConstants.WHITELISTED_ROUTES).permitAll()
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
