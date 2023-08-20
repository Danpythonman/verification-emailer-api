package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.NoOpEmailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("no-op")
public class NoOpEmailerConfig {

    private final Logger log =
        LoggerFactory.getLogger(NoOpEmailerConfig.class);

    @Bean
    public NoOpEmailer noOpEmailer() {
        this.log.info("Initializing No-op Emailer");
        return new NoOpEmailer();
    }

}
