package com.danieldigiovanni.email.config;

import com.danieldigiovanni.email.emailer.NoOpEmailer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    value = "emailer-method",
    havingValue = "no-op",
    matchIfMissing = true
)
public class NoOpEmailerConfig {

    @Bean
    public NoOpEmailer noOpEmailer() {
        return new NoOpEmailer();
    }

}
