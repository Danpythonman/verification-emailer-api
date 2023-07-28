package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.MailtrapEmailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@ConditionalOnProperty(
    value = "emailer-method",
    havingValue = "mailtrap"
)
public class MailtrapEmailerConfig {

    private final String fromAddress;
    private final String emailHtmlTemplate;
    private final JavaMailSender mailSender;

    @Autowired
    public MailtrapEmailerConfig(
        @Value("${emailer.api.from-address}") String fromAddress,
        @Qualifier("emailHtmlTemplate") String emailHtmlTemplate,
        JavaMailSender mailSender
    ) {
        this.fromAddress = fromAddress;
        this.emailHtmlTemplate = emailHtmlTemplate;
        this.mailSender = mailSender;
    }

    @Bean
    public MailtrapEmailer mailtrapEmailer() {
        return new MailtrapEmailer(
            this.fromAddress,
            this.emailHtmlTemplate,
            this.mailSender
        );
    }

}