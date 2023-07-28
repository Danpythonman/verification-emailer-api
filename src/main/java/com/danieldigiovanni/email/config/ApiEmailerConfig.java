package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.ApiEmailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(
    value = "emailer-method",
    havingValue = "api"
)
public class ApiEmailerConfig {

    private final String fromAddress;
    private final String sendEmailUrl;
    private final String refreshTokenUrl;
    private final String authScheme;
    private final String emailHtmlTemplate;
    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(ApiEmailerConfig.class);

    @Autowired
    public ApiEmailerConfig(
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
    public ApiEmailer emailer() {
        this.log.info("Initializing API Emailer");
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
