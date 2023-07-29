package com.danieldigiovanni.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Configuration
public class EmailTemplateConfig {

    @Bean("emailHtmlTemplate")
    public String emailHtmlTemplate() throws IOException {
        InputStream inputStream =
            new ClassPathResource("emailTemplate.html").getInputStream();
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
            .collect(Collectors.joining(System.lineSeparator()));
    }

}
