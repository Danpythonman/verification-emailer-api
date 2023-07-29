package com.danieldigiovanni.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class EmailTemplateConfig {

    @Bean("emailHtmlTemplate")
    public String emailHtmlTemplate() throws IOException {
        Path path = new ClassPathResource("emailTemplate.html")
            .getFile()
            .toPath();

        return Files.readString(path, StandardCharsets.UTF_8);
    }

}
