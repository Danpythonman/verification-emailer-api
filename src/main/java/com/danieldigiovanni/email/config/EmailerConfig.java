package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.ApiEmailer;
import com.danieldigiovanni.email.emailer.Emailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmailerConfig {

    private final String fromAddress;
    private final String sendEmailUrl;
    private final String refreshTokenUrl;
    private final String authScheme;
    private final String emailHtmlTemplate;
    private final RestTemplate restTemplate;

    @Autowired
    public EmailerConfig(
        @Value("${emailer.api.from-address}") String fromAddress,
        @Value("${emailer.api.send-email-url}") String sendEmailUrl,
        @Value("${emailer.api.refresh-token-url}") String refreshTokenUrl,
        @Value("${emailer.api.auth-scheme}") String authScheme,
        @Qualifier("emailHtmlTemplate") String emailHtmlTemplate,
        RestTemplate restTemplate
    ) {
        this.fromAddress = fromAddress;
        this.sendEmailUrl = sendEmailUrl;
        this.refreshTokenUrl = refreshTokenUrl;
        this.authScheme = authScheme;
        this.emailHtmlTemplate = emailHtmlTemplate;
        this.restTemplate = restTemplate;
    }

    @Bean
    public Emailer emailer() {
        return new ApiEmailer(
            this.fromAddress,
            this.sendEmailUrl,
            this.refreshTokenUrl,
            this.authScheme,
            this.emailHtmlTemplate,
            this.restTemplate
        );
    }

}
