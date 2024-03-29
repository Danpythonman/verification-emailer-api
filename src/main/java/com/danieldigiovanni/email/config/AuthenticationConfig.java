package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.auth.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class AuthenticationConfig {

    private final CustomerDetailsService customerDetailsService;
    private final List<String> whitelistedRoutes;

    @Autowired
    public AuthenticationConfig(
        CustomerDetailsService customerDetailsService,
        @Value("${whitelisted-routes}") String[] whitelistedRoutes
    ) {
        this.customerDetailsService = customerDetailsService;
        this.whitelistedRoutes = List.of(whitelistedRoutes);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider
            = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(
            this.customerDetailsService
        );
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("whitelistedRoutes")
    public List<String> whitelistedRoutes() {
        return this.whitelistedRoutes;
    }

}
