package com.danieldigiovanni.email.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSenderConfig {

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;

    @Autowired
    public JavaMailSenderConfig(
        @Value("${emailer.mailtrap.host}") String host,
        @Value("${emailer.mailtrap.port}") Integer port,
        @Value("${emailer.mailtrap.username}") String username,
        @Value("${emailer.mailtrap.password}") String password
    ) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.host);
        mailSender.setPort(this.port);
        mailSender.setUsername(this.username);
        mailSender.setPassword(this.password);

        return mailSender;
    }

}
