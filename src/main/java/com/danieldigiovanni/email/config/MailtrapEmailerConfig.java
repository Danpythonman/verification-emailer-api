package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.MailtrapEmailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@Profile("mailtrap")
public class MailtrapEmailerConfig {

    private final String fromAddress;
    private final String emailHtmlTemplate;
    private final JavaMailSender mailSender;
    private final Logger log =
        LoggerFactory.getLogger(MailtrapEmailerConfig.class);

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
        this.log.info("Initializing Mailtrap Emailer");
        return new MailtrapEmailer(
            this.fromAddress,
            this.emailHtmlTemplate,
            this.mailSender
        );
    }

}
