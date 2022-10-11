package com.amazon.sample.ui.config.metadata;

import com.amazon.sample.ui.services.Metadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultMetadataConfiguration {

    @Bean
    public Metadata metadata() {
        return new Metadata("Local").add("environment", "local");
    }
}
